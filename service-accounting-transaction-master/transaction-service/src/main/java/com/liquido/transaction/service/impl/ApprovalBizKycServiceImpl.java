package com.liquido.transaction.service.impl;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.validation.constraints.NotNull;

import com.liquido.core.common.utils.JsonUtil;
import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.crm.CrmApis;
import com.liquido.crm.enums.KycBizApprovalStatusEnum;
import com.liquido.crm.pojo.vo.kyc.KycBizApprovalCallbackVo;
import com.liquido.transaction.common.properties.LarkProperties;
import com.liquido.transaction.enums.ApprovalNodeStatusEnum;
import com.liquido.transaction.enums.ApprovalNodeTypeEnum;
import com.liquido.transaction.enums.ApprovalOptionTypeEnum;
import com.liquido.transaction.enums.ApprovalStatusEnum;
import com.liquido.transaction.enums.ApprovalTypeEnum;
import com.liquido.transaction.exception.TransactionExceptionCode;
import com.liquido.transaction.pojo.bo.ApprovalBizKycBo;
import com.liquido.transaction.pojo.bo.event.ApprovalCallBackEventBo;
import com.liquido.transaction.pojo.dto.ApprovalBizKycDto;
import com.liquido.transaction.pojo.dto.LarkCreateApprovalInstancesDto;
import com.liquido.transaction.pojo.entity.Approval;
import com.liquido.transaction.pojo.entity.ApprovalBizKyc;
import com.liquido.transaction.pojo.vo.ApprovalNodeVo;
import com.liquido.transaction.pojo.vo.CreateApprovalKycVo;
import com.liquido.transaction.repository.ApprovalBizKycRepository;
import com.liquido.transaction.repository.ApprovalRepository;
import com.liquido.transaction.service.ApprovalBizKycService;
import com.liquido.transaction.service.ApprovalNodeService;
import com.liquido.transaction.service.LarkApprovalService;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApprovalBizKycServiceImpl implements ApprovalBizKycService, InitializingBean {

    private final LarkProperties.ApprovalKyc approvalKyc;

    private final ApprovalBizKycRepository approvalBizKycRepository;

    private final ApprovalRepository approvalRepository;

    private final LarkApprovalService larkApprovalService;

    private final ApprovalNodeService approvalNodeService;

    private final CrmApis.CrmFeign crmFeign;

    @Override
    public void afterPropertiesSet() throws Exception {
        String kycApprovalCode = approvalKyc.getApprovalCode();
        List<LarkProperties.ColumnBo> kycFormMap = approvalKyc.getFormMap();

        Assert.hasText(kycApprovalCode, "kycApprovalCode must not blank!");
        Assert.notEmpty(kycFormMap, "kycFormMap must not empty!");
    }

    @Override
    public ApprovalBizKycBo retrieveKycApproval(final Long approvalId) {
        final ApprovalBizKyc approvalBizKyc = approvalBizKycRepository.findByApprovalId(approvalId);
        return convertApprovalBizKycBo(approvalBizKyc);
    }

    private ApprovalBizKycBo convertApprovalBizKycBo(final ApprovalBizKyc approvalBizKyc) {
        if (null == approvalBizKyc) {
            return null;
        }

        final List<String> complianceOwners = revertStringList(
                approvalBizKyc.getOwnerTeams());

        return ApprovalBizKycBo.builder()
                .id(approvalBizKyc.getId())
                .linkId(approvalBizKyc.getLinkId())
                .approvalId(approvalBizKyc.getApproval().getId())
                .fullAccessLink(approvalBizKyc.getFullAccessLink())
                .formSubmissionTime(approvalBizKyc.getFormSubmissionTime())
                .merchantTradingName(approvalBizKyc.getMerchantTradingName())
                .merchantIncorporationCountry(approvalBizKyc.getMerchantIncorporationCountry())
                .complianceOwners(complianceOwners)
                .createdTime(approvalBizKyc.getCreatedTime())
                .updatedTime(approvalBizKyc.getUpdatedTime())
                .build();
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public ApprovalBizKycDto createKycApproval(final CreateApprovalKycVo approvalKycVo) {
        log.info("creating kyc approval for linkId {}", approvalKycVo.getLinkId());

        Long linkId = approvalKycVo.getLinkId();
        if (approvalBizKycRepository.existsByLinkId(linkId)) {
            log.error("duplicate kyc approval creation, linkId: {}", linkId);
            return ApprovalBizKycDto.builder().build();
        }
        final LarkCreateApprovalInstancesDto larkApprovalInstance =
                createLarkApprovalInstance(approvalKycVo);

        return createApprovalBizKyc(approvalKycVo, larkApprovalInstance.getInstanceCode());
    }


    @NotNull
    private LarkCreateApprovalInstancesDto createLarkApprovalInstance(
            final CreateApprovalKycVo approvalKycVo
    ) {
        final String kycApprovalCode = approvalKyc.getApprovalCode();
        final Map<String, String> kycFormMap = approvalKyc.getFormMap()
                .stream()
                .collect(Collectors.toMap(LarkProperties.ColumnBo::getColumn,
                        LarkProperties.ColumnBo::getLarkColumn));

        return larkApprovalService.createApprovalInstance(
                kycApprovalCode,
                kycFormMap,
                approvalKycVo
        );
    }

    /**
     * create and save kyc approval biz data.
     * <p> basically for retrieving data for duplicate check and result callback handle
     *
     * @param approvalKycVo        biz approval form data
     * @param approvalInstanceCode lark approval unique instance code,
     *                             which is associated with internal biz approval
     * @return {@link ApprovalBizKycDto}
     */
    private ApprovalBizKycDto createApprovalBizKyc(final CreateApprovalKycVo approvalKycVo,
            final String approvalInstanceCode) {
        // init internal biz approval, biz approval is associated with lark approval instance
        // (for biz approval data retrieval)
        final Approval approval = initializeInternalBizApproval(approvalInstanceCode);

        // init approval node
        // internal biz approval component requires, but for kyc biz, don't care about this
        initializeBizApprovalNode(approval);

        final ApprovalBizKyc approvalBizKyc = saveKycApprovalBizData(approvalKycVo, approval);

        notifyKycApprovalProgress(approvalBizKyc);

        log.info("kyc approval created, linkId: {}; approvalId: {}; approvalBizKycId: {}",
                approvalKycVo.getLinkId(), approvalBizKyc.getId(), approval.getId());

        return ApprovalBizKycDto.builder()
                .id(approvalBizKyc.getId())
                .approvalId(approval.getId())
                .build();
    }

    private ApprovalBizKyc saveKycApprovalBizData(final CreateApprovalKycVo approvalKycVo,
            final Approval approval) {
        ApprovalBizKyc approvalBizKyc = ApprovalBizKyc.builder()
                .linkId(approvalKycVo.getLinkId())
                .fullAccessLink(approvalKycVo.getFullAccessLink())
                .approval(approval)
                .formSubmissionTime(approvalKycVo.getFormSubmissionTime())
                .merchantTradingName(approvalKycVo.getMerchantTradingName())
                .merchantIncorporationCountry(approvalKycVo.getMerchantIncorporationCountry())
                .processingCountries(convertStringList(approvalKycVo.getProcessingCountries()))
                .build();
        approvalBizKycRepository.saveAndFlush(approvalBizKyc);
        return approvalBizKyc;
    }

    private Approval initializeInternalBizApproval(final String larkApprovalInstanceCode) {
        final Approval approval = Approval.builder()
                .type(ApprovalTypeEnum.KYC)
                .status(ApprovalStatusEnum.PROCESSING)
                .instanceCode(larkApprovalInstanceCode)
                .revokeFlag(true).build();
        approvalRepository.saveAndFlush(approval);
        return approval;
    }

    private void initializeBizApprovalNode(final Approval approval) {
        // ensure that the start node must start before any other nodes
        final LocalDateTime now = LocalDateTimeUtil.nowUtc().minusSeconds(5);
        final ApprovalNodeVo approvalNodeVo = ApprovalNodeVo.builder()
                .approvalId(approval.getId())
                .approvalInstanceCode(approval.getInstanceCode())
                .nodeId(UUID.randomUUID().toString())
                .nodeName(ApprovalNodeTypeEnum.START.getName())
                .nodeType(ApprovalNodeTypeEnum.START)
                .startTime(now)
                .endTime(now)
                .approvalNodeStatus(ApprovalNodeStatusEnum.APPROVED)
                .pendingApprovers(List.of())
                .approvalOptionType(ApprovalOptionTypeEnum.AND)
                .doneApprovers(List.of())
                .build();
        approvalNodeService.saveOrUpdate(approvalNodeVo);
    }

    @Override
    public void onKycApprovalCallback(final ApprovalCallBackEventBo callBackEventBo) {
        Long approvalId = callBackEventBo.getApprovalId();
        Approval approval = approvalRepository.findById(approvalId)
                .orElse(null);

        if (null == approval) {
            log.error("approval data of id [{}] not found", approvalId);
            return;
        }

        tryHandleApprovalCallback(approval);
    }

    private void tryHandleApprovalCallback(final Approval approval) {
        final Long approvalId = approval.getId();
        final ApprovalStatusEnum status = approval.getStatus();

        if (!supportsStatus(status)) {
            log.info("kyc approval {} callback status is {}, continue approval flow",
                    approvalId, status.getCode());
            return;
        }

        notifyKycApprovalProgress(approvalId);
    }

    private boolean supportsStatus(final ApprovalStatusEnum status) {
        // only process these two status
        return status == ApprovalStatusEnum.COMPLETED
                || status == ApprovalStatusEnum.REJECTED;
    }

    private void notifyKycApprovalProgress(final Long approvalId) {
        final ApprovalBizKyc approvalBizKyc = approvalBizKycRepository.findByApprovalId(approvalId);
        notifyKycApprovalProgress(approvalBizKyc);
    }

    private void notifyKycApprovalProgress(final ApprovalBizKyc approvalBizKyc) {
        Long approvalId = approvalBizKyc.getApproval().getId();
        log.info("kyc compliance registration form approval finished approvalId: {}", approvalId);

        final KycBizApprovalCallbackVo callbackVo = KycBizApprovalCallbackVo.builder()
                .linkId(approvalBizKyc.getLinkId())
                .status(convertApprovalStatus(approvalBizKyc.getApproval().getStatus()))
                .build();

        ResponseDto<Void> resp = crmFeign.onKycBizApprovalCallback(callbackVo);
        if (resp.isFail()) {
            log.error("crm kyc approval callback failed, msg: {}", resp.getMsg());
            // TODO: 2023/8/25 lark monitor notification
        }
    }

    private KycBizApprovalStatusEnum convertApprovalStatus(
            final ApprovalStatusEnum approvalStatus) {
        if (approvalStatus == ApprovalStatusEnum.PROCESSING) {
            return KycBizApprovalStatusEnum.CREATED;
        } else if (approvalStatus == ApprovalStatusEnum.COMPLETED) {
            return KycBizApprovalStatusEnum.APPROVED;
        } else if (approvalStatus == ApprovalStatusEnum.REJECTED) {
            return KycBizApprovalStatusEnum.REJECTED;
        }

        throw TransactionExceptionCode.CREATE_APPROVAL_KYC_ERROR
                .exception(approvalStatus.getCode());
    }

    private JsonNode convertStringList(final List<String> list) {
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }

        return JsonUtil.getObjectMapper().valueToTree(list);
    }

    private List<String> revertStringList(final JsonNode jsonNode) {
        if (null == jsonNode) {
            return Collections.emptyList();
        }
        ObjectMapper objectMapper = JsonUtil.getObjectMapper();
        JavaType javaType = objectMapper.getTypeFactory()
                .constructParametricType(List.class, String.class);
        try {
            return objectMapper.treeToValue(jsonNode, javaType);
        } catch (Exception e) {
            log.error("resolve compliance owners json error", e);
            return Collections.emptyList();
        }
    }


}
