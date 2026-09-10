package com.liquido.transaction.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import javax.transaction.Transactional;

import com.liquido.base.enums.BusinessTypeEnum;
import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.pojo.dto.SubMerchantDto;
import com.liquido.core.common.cache.RedisDistLock;
import com.liquido.core.common.exception.CommonExceptionCode;
import com.liquido.core.common.snowflake.SnowflakeIdUtil;
import com.liquido.core.common.utils.AmountUtil;
import com.liquido.core.common.utils.BeanCopierUtil;
import com.liquido.core.common.utils.CheckResponseUtil;
import com.liquido.core.common.utils.JsonUtil;
import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.core.mvc.vo.PageVo;
import com.liquido.statement.StatementApis;
import com.liquido.statement.pojo.dto.AccountDto;
import com.liquido.statement.pojo.dto.ApprovalApplyDto;
import com.liquido.statement.pojo.dto.RealTimeExchangeRateDto;
import com.liquido.statement.pojo.vo.ApprovalApplyVo;
import com.liquido.statement.pojo.vo.ApprovalRollBackVo;
import com.liquido.statement.pojo.vo.QueryAccountInfoVo;
import com.liquido.statement.pojo.vo.QueryAccountVo;
import com.liquido.statement.pojo.vo.QueryLatestExchangeRateVo;
import com.liquido.transaction.common.Constant;
import com.liquido.transaction.common.properties.LarkProperties;
import com.liquido.transaction.enums.ApprovalBizExchangeStatusEnum;
import com.liquido.transaction.enums.ApprovalNodeStatusEnum;
import com.liquido.transaction.enums.ApprovalNodeTypeEnum;
import com.liquido.transaction.enums.ApprovalOptionTypeEnum;
import com.liquido.transaction.enums.ApprovalStatusEnum;
import com.liquido.transaction.enums.ApprovalTypeEnum;
import com.liquido.transaction.enums.ExchangeAccountTypeEnum;
import com.liquido.transaction.enums.FeeModeEnum;
import com.liquido.transaction.exception.TransactionExceptionCode;
import com.liquido.transaction.feign.BaseService;
import com.liquido.transaction.pojo.bo.ApprovalBizExchangeConfigBo;
import com.liquido.transaction.pojo.bo.ApprovalConfigAccountBo;
import com.liquido.transaction.pojo.bo.ApprovalUserInfoBo;
import com.liquido.transaction.pojo.bo.BeneficiaryAccountBo;
import com.liquido.transaction.pojo.bo.CreateBizExchangeBo;
import com.liquido.transaction.pojo.bo.MerchantRatioBo;
import com.liquido.transaction.pojo.bo.PreProcessBizExchangeBo;
import com.liquido.transaction.pojo.bo.event.ApprovalBizExchangeFinishEvent;
import com.liquido.transaction.pojo.bo.event.ApprovalBizExchangeFinishEventBo;
import com.liquido.transaction.pojo.bo.event.ApprovalFinishEvent;
import com.liquido.transaction.pojo.bo.event.ApprovalFinishEventBo;
import com.liquido.transaction.pojo.dto.AppendixDto;
import com.liquido.transaction.pojo.dto.ApprovalBizExchangeDto;
import com.liquido.transaction.pojo.dto.ApprovalNodeSimpleDto;
import com.liquido.transaction.pojo.dto.CalculateExchangeAmountDto;
import com.liquido.transaction.pojo.dto.LarkCreateApprovalInstancesDto;
import com.liquido.transaction.pojo.dto.QueryApprovalBizExchangeDto;
import com.liquido.transaction.pojo.dto.QueryApprovalConfigDto;
import com.liquido.transaction.pojo.entity.Approval;
import com.liquido.transaction.pojo.entity.ApprovalBizExchange;
import com.liquido.transaction.pojo.entity.ApprovalConfig;
import com.liquido.transaction.pojo.form.ApprovalBizExchangeForm;
import com.liquido.transaction.pojo.form.CreateApprovalBizExchangeForm;
import com.liquido.transaction.pojo.mapper.ModelMapper;
import com.liquido.transaction.pojo.vo.AgreeApprovalInstancesNodeVo;
import com.liquido.transaction.pojo.vo.ApprovalNodeVo;
import com.liquido.transaction.pojo.vo.CalculateExchangeAmountVo;
import com.liquido.transaction.pojo.vo.CreateApprovalBizExchangeVo;
import com.liquido.transaction.pojo.vo.CreateApprovalConfigVo;
import com.liquido.transaction.pojo.vo.LarkApprovalInstancesVo;
import com.liquido.transaction.pojo.vo.PageApprovalBizExchangeVo;
import com.liquido.transaction.pojo.vo.QueryApprovalBizExchangeVo;
import com.liquido.transaction.pojo.vo.SubmitOfflineProofBizExchangeVo;
import com.liquido.transaction.repository.ApprovalBizExchangeRepository;
import com.liquido.transaction.repository.ApprovalConfigRepository;
import com.liquido.transaction.repository.ApprovalRepository;
import com.liquido.transaction.service.AppendixService;
import com.liquido.transaction.service.ApprovalBizExchangeService;
import com.liquido.transaction.service.ApprovalNodeService;
import com.liquido.transaction.service.LarkApprovalService;
import com.liquido.transaction.service.monitor.LarkRobotMonitor;

import com.cosium.spring.data.jpa.entity.graph.domain.EntityGraphUtils;
import com.github.wenhao.jpa.Specifications;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackOn = Exception.class)
public class ApprovalBizExchangeServiceImpl implements ApprovalBizExchangeService {

    private final ApprovalBizExchangeRepository approvalBizExchangeRepository;
    private final ApprovalConfigRepository<ApprovalBizExchangeConfigBo> approvalConfigRepository;
    private final ModelMapper modelMapper;
    private final LarkProperties.ApprovalExchange approvalExchangeProperties;
    private final LarkApprovalService larkApprovalService;
    private final ApprovalRepository approvalRepository;
    private final ApprovalNodeService approvalNodeService;
    private final StatementApis.StatementFeign statementFeign;
    private final AppendixService appendixService;
    private final BaseService baseService;
    private final ApplicationEventPublisher publisher;
    private final LarkRobotMonitor larkRobotMonitor;
    private final RedisDistLock redisDistLock;

    @Override
    public ApprovalBizExchange findById(final Long id) {
        return approvalBizExchangeRepository.findById(id)
                .orElseThrow(CommonExceptionCode.DATA_NOT_FOUND::exception);
    }

    @Override
    public ApprovalBizExchange findByApprovalId(final Long approvalId) {
        return approvalBizExchangeRepository.findOne(
                        Specifications.<ApprovalBizExchange>and()
                                .eq("approval.id", approvalId).build(),
                        EntityGraphUtils.fromAttributePaths("approval"))
                .orElseThrow(CommonExceptionCode.DATA_NOT_FOUND::exception);
    }

    @Override
    public ApprovalBizExchange findByIdAndMerchantId(final Long id, final Long merchantId) {
        final Specification<ApprovalBizExchange> spec =
                Specifications.<ApprovalBizExchange>and().eq("id", id)
                        .eq(Objects.nonNull(merchantId), "merchantId", merchantId).build();
        return approvalBizExchangeRepository.findOne(spec)
                .orElseThrow(CommonExceptionCode.DATA_NOT_FOUND::exception);
    }

    @SuppressWarnings("unchecked")
    @Override
    public QueryApprovalConfigDto<ApprovalBizExchangeConfigBo> queryApprovalConfig(
            final Long accountId) {
        return modelMapper.convert(approvalConfigRepository.findByAccountIdAndApprovalType(
                accountId, ApprovalTypeEnum.EXCHANGE));
    }

    @Override
    public ApprovalBizExchangeDto createApprovalBizExchange(
            final CreateApprovalBizExchangeVo vo) {

        // Ensure that the start node is before of other node.
        final LocalDateTime now = LocalDateTimeUtil.nowUtc().minusSeconds(5);
        vo.setApprovalBizExchangeId(SnowflakeIdUtil.generate());
        LarkCreateApprovalInstancesDto dto = new LarkCreateApprovalInstancesDto();

        log.info("create approval biz exchange param vo->{}", vo);

        /*pre-process */
        final PreProcessBizExchangeBo bo = preProcessBizExchange(vo);
        CreateBizExchangeBo createBizExchangeBo = new CreateBizExchangeBo();
        try {
            // step 1: create lark approval about exchange
            dto = this.createdApprovalExchangeInstance(vo, bo);

            /* step 2:create approval and approval node */
            final Approval approval = this.createdApprovalAndApprovalNode(vo, dto, now);

            /* step 3:create biz bizExchange */
            createBizExchangeBo = createdBizExchange(vo, bo, approval);
            return createBizExchangeBo.getBizExchangeDto();
        } catch (Exception e) {
            log.error("create approval biz exchange error", e);
            this.createBizExchangeFailPostProcessing(dto, vo, createBizExchangeBo.getApplyDto(),
                    e.getMessage());
            throw TransactionExceptionCode.CREATE_APPROVAL_EXCHANGE_ERROR.exception(e.getMessage());
        }
    }

    public void createBizExchangeFailPostProcessing(final LarkCreateApprovalInstancesDto dto,
                                                    final CreateApprovalBizExchangeVo vo,
                                                    final ApprovalApplyDto approvalApplyDto,
                                                    final String errorMessage) {

        try {
            log.info("created biz exchange fail post processing instanceCode={}",
                    dto.getInstanceCode());
            if (Objects.nonNull(approvalApplyDto)
                    && Objects.nonNull(approvalApplyDto.getTransactionId())) {
                final ResponseDto<Void> responseDto = statementFeign.approvalRollBack(
                        ApprovalRollBackVo.builder()
                                .bizTypeCode(BusinessTypeEnum.EXCHANGE)
                                .transactionId(approvalApplyDto.getTransactionId())
                                .requestId(String.valueOf(SnowflakeIdUtil.generate()))
                                .merchantId(vo.getMerchantId())
                                .accountId(vo.getAccountId())
                                .amount(vo.getExchangeAmount())
                                .build());
                CheckResponseUtil.checkResponse(responseDto);
            }
        } catch (Exception e) {
            log.error("created biz exchange fail post processing", e);
            larkRobotMonitor.error("Create Approval Biz Exchange Error",
                    "Exchange Approval On Exchange Amount Roll Back Fail",
                    e.getMessage());
        } finally {
            if (StringUtils.isNotBlank(dto.getInstanceCode())) {
                larkApprovalService.cancelApprovalInstance(LarkApprovalInstancesVo.builder()
                        .approvalCode(approvalExchangeProperties.getApprovalCode())
                        .instanceCode(dto.getInstanceCode()).build());
            }

            // send lark alarm
            final StringBuilder sb = new StringBuilder()
                    .append("**Merchant:** ")
                    .append(vo.getMerchantName()).append("\\n")
                    .append("**Country :** ")
                    .append(vo.getCountryCode().getCountryName()).append("\\n")
                    .append("**TransactionTypeCode:** ")
                    .append(vo.getTransactionTypeCode().getCode()).append("\\n")
                    .append("**Amount:** ")
                    .append(AmountUtil.centToYuan(vo.getExchangeAmount())).append("\\n")
                    .append("**Currency:** ")
                    .append(vo.getExchangeCurrency().getCode()).append("\\n")
                    .append("**ApplicationTime:** ")
                    .append(LocalDateTimeUtil.nowUtcToLocal("UTC+8")
                            .format(LocalDateTimeUtil.FORMAT_DATETIME)).append("\\n");

            larkRobotMonitor.error("Create Approval Biz Exchange Error", sb.toString(),
                    errorMessage);
        }
    }

    @Override
    public void submitOfflineProofBizExchange(final SubmitOfflineProofBizExchangeVo vo) {

        /*pre-process */
        final PreProcessBizExchangeBo bo = preProcessOfflineSubmitProof(vo);

        /* approval node agree */
        offlineProofApprovalApprove(bo);
    }

    private void offlineProofApprovalApprove(final PreProcessBizExchangeBo bo) {

        final ApprovalBizExchangeForm form =
                modelMapper.convertCreateApprovalBizExchangeVo(bo.getApprovalBizExchange());
        form.setAppendixIds(bo.getAppendixLarkCodes());

        final List<String> result = Optional.ofNullable(bo)
                .map(PreProcessBizExchangeBo::getApprovalConfigAccountBo)
                .map(ApprovalConfigAccountBo::getAccountInfos)
                .orElse(List.of())
                .stream()
                .map(v -> v.getLabel() + ": " + v.getValue())
                .collect(Collectors.toList());
        form.setBeneficiaryAccount(String.join("\n", result));

        final Map<String, String> formMap = approvalExchangeProperties.getFormMap().stream()
                .collect(Collectors.toMap(LarkProperties.ColumnBo::getColumn,
                        LarkProperties.ColumnBo::getLarkColumn));

        larkApprovalService.agreeApprovalInstanceNode(AgreeApprovalInstancesNodeVo.builder()
                .approvalCode(approvalExchangeProperties.getApprovalCode())
                .formData(form)
                .instanceCode(bo.getApprovalBizExchange().getApproval().getInstanceCode())
                .formMap(formMap)
                .build());

        final ApprovalBizExchange approvalBizExchange = bo.getApprovalBizExchange();
        approvalBizExchange.setMerchantAccount(BeneficiaryAccountBo.builder()
                .accountInfos(bo.getApprovalConfigAccountBo().getAccountInfos())
                .id(bo.getApprovalConfigAccountBo().getId()).build());
        approvalBizExchange.setStatus(ApprovalBizExchangeStatusEnum.PROCESSING);

        approvalBizExchangeRepository.save(approvalBizExchange);
    }

    private PreProcessBizExchangeBo preProcessOfflineSubmitProof(
            final SubmitOfflineProofBizExchangeVo vo) {

        final ApprovalBizExchange exchange = approvalBizExchangeRepository.findById(vo.getId())
                .orElseThrow(CommonExceptionCode.DATA_NOT_FOUND::exception);

        if (ApprovalBizExchangeStatusEnum.WAIT_PROOF != exchange.getStatus()) {
            throw TransactionExceptionCode.SUBMIT_PROOF_ERROR.exception();
        }

        final QueryApprovalConfigDto<ApprovalBizExchangeConfigBo> config =
                this.queryApprovalConfig(vo.getAccountId());

        final ApprovalConfigAccountBo approvalConfigAccountBo =
                config.getConfig().getSupportConfig().get(exchange.getTargetCurrency())
                        .getMerchantAccountInfo().stream()
                        .filter(bef -> vo.getBeneficiaryAccountId().equals(bef.getId()))
                        .findFirst()
                        .orElseThrow(CommonExceptionCode.DATA_NOT_FOUND::exception);

        // Get the appendix lark code
        final List<String> appendixLarkCodes =
                appendixService.findAllByIds(vo.getAppendixIds()).stream()
                        .map(AppendixDto::getReferenceCode).collect(Collectors.toList());

        return PreProcessBizExchangeBo.builder()
                .approvalConfigAccountBo(approvalConfigAccountBo)
                .approvalBizExchange(exchange)
                .appendixLarkCodes(appendixLarkCodes)
                .build();
    }

    private BigDecimal calculateSmallFee(
            final CalculateExchangeAmountDto result,
            final ApprovalBizExchangeConfigBo.SupportConfigBo config) {

        final ApprovalBizExchangeConfigBo.ExchangeFeeBo fee = config.getExchangeFee();

        final BigDecimal smallFee = fee.getSmallFee();
        final Boolean smallFeeFlag = fee.getOpenFlag();
        final BigDecimal smallFeeLimit = fee.getSmallFeeLimit();
        final CurrencyEnum smallFeeCurrency = fee.getCurrency();

        if (Objects.isNull(smallFee) || Objects.isNull(smallFeeFlag)
                || Objects.isNull(smallFeeCurrency) || Objects.isNull(smallFeeLimit)) {
            throw TransactionExceptionCode.APPROVAL_CONFIG_MISSING_ERROR.exception();
        }

        if (Boolean.TRUE.equals(smallFeeFlag)) {

            if (smallFeeCurrency != result.getReceiptCurrencyCode()
                    && smallFeeCurrency != result.getDeductionCurrencyCode()) {
                throw TransactionExceptionCode.EXCHANGE_FEE_CURRENCY_ERROR.exception();
            }
            if ((smallFeeCurrency == result.getReceiptCurrencyCode()
                    && result.getReceiptAmount().compareTo(smallFeeLimit) < 0)
                    || (smallFeeCurrency == result.getDeductionCurrencyCode()
                    && result.getDeductionAmount().compareTo(smallFeeLimit) < 0)) {
                return smallFee;
            }
        }
        return BigDecimal.ZERO;
    }

    private PreProcessBizExchangeBo preProcessBizExchange(
            final CreateApprovalBizExchangeVo vo) {

        final QueryApprovalConfigDto<ApprovalBizExchangeConfigBo> config =
                this.queryApprovalConfig(vo.getAccountId());

        final ApprovalBizExchangeConfigBo bizConfig = config.getConfig();

        if (!bizConfig.getSupportCurrency().contains(vo.getTargetCurrency())) {
            throw TransactionExceptionCode.EXCHANGE_CURRENCY_NOT_SUPPORT.exception();
        }

        final ApprovalBizExchangeConfigBo.SupportConfigBo configBo =
                bizConfig.getSupportConfig().get(vo.getTargetCurrency());

        ApprovalConfigAccountBo approvalConfigAccountBo;
        if (ExchangeAccountTypeEnum.OFFLINE == vo.getExchangeAccountType()) {
            approvalConfigAccountBo = configBo.getOwnerAccountInfo().stream()
                    .filter(bef -> vo.getBeneficiaryAccountId().equals(bef.getId()))
                    .findFirst()
                    .orElseThrow(CommonExceptionCode.DATA_NOT_FOUND::exception);
        } else {
            approvalConfigAccountBo = configBo.getMerchantAccountInfo().stream()
                    .filter(bef -> vo.getBeneficiaryAccountId().equals(bef.getId()))
                    .findFirst()
                    .orElseThrow(CommonExceptionCode.DATA_NOT_FOUND::exception);
        }

        final SubMerchantDto subMerchant = StringUtils.isBlank(vo.getSubMerchantId()) ? null
                : baseService.getSubMerchant(vo.getMerchantId(), vo.getSubMerchantId());

        return PreProcessBizExchangeBo.builder()
                .approvalConfigAccountBo(approvalConfigAccountBo)
                .config(configBo)
                .subMerchant(subMerchant)
                .build();
    }

    private LarkCreateApprovalInstancesDto createdApprovalExchangeInstance(
            final CreateApprovalBizExchangeVo vo, final PreProcessBizExchangeBo bo) {

        log.info("create approval exchange vo={},bo={}", vo, bo);

        final CreateApprovalBizExchangeForm form =
                modelMapper.convertCreateApprovalBizExchangeVo(vo);
        form.setCustomExchangeRate(bo.getConfig().getCustomExchangeRate());

        form.setSubMerchantName(Objects.isNull(bo.getSubMerchant())
                ? null : bo.getSubMerchant().getCommercialName());

        if (ExchangeAccountTypeEnum.OFFLINE != vo.getExchangeAccountType()) {
            final List<String> result = Optional.ofNullable(bo)
                    .map(PreProcessBizExchangeBo::getApprovalConfigAccountBo)
                    .map(ApprovalConfigAccountBo::getAccountInfos)
                    .orElse(List.of())
                    .stream()
                    .map(v -> v.getLabel() + ": " + v.getValue())
                    .collect(Collectors.toList());
            form.setBeneficiaryAccount(String.join("\n", result));
        }

        final Map<String, String> formMap = approvalExchangeProperties.getFormMap().stream()
                .collect(Collectors.toMap(LarkProperties.ColumnBo::getColumn,
                        LarkProperties.ColumnBo::getLarkColumn));

        return larkApprovalService.createApprovalInstance(approvalExchangeProperties
                .getApprovalCode(), formMap, form);
    }

    private CreateBizExchangeBo createdBizExchange(
            final CreateApprovalBizExchangeVo vo, final PreProcessBizExchangeBo bo,
            final Approval approval) {

        final ApprovalBizExchange approvalBizExchange =
                modelMapper.convertCreateApprovalBizExchangeToEntity(vo);

        approvalBizExchange.setApproval(approval);

        approvalBizExchange.setActualExchangeAmountTargetCurrency(
                approvalBizExchange.getExchangeAmountTargetCurrency());
        approvalBizExchange.setStatus(
                getExchangeStartStatus(approvalBizExchange, bo.getConfig()));
        approvalBizExchange.setCustomExchangeRate(bo.getConfig().getCustomExchangeRate());
        approvalBizExchange.setContractConfirmFlag(false);
        approvalBizExchange.setNeedConfirmContract(bo.getConfig().getNeedConfirmContract());
        approvalBizExchange.setFeeFlag(bo.getConfig().getExchangeFee().getOpenFlag());
        approvalBizExchange.setFeeMode(bo.getConfig().getExchangeFee().getFeeMode());
        approvalBizExchange.setFeeValue(bo.getConfig().getExchangeFee().getFeeValue());
        approvalBizExchange.setConfigSmallFee(bo.getConfig().getExchangeFee().getSmallFee());
        approvalBizExchange.setSmallFeeLimit(
                bo.getConfig().getExchangeFee().getSmallFeeLimit());
        approvalBizExchange.setCustomFeeFlag(vo.getCustomFee());
        approvalBizExchange.setExtraCost(BigDecimal.ZERO);
        approvalBizExchange.setSubMerchantId(Objects.isNull(bo.getSubMerchant())
                ? null : bo.getSubMerchant().getSubMerchantId());

        final BeneficiaryAccountBo accountBo = BeneficiaryAccountBo.builder()
                .accountInfos(bo.getApprovalConfigAccountBo().getAccountInfos())
                .id(bo.getApprovalConfigAccountBo().getId()).build();
        if (ExchangeAccountTypeEnum.OFFLINE == approvalBizExchange.getExchangeAccountType()) {
            approvalBizExchange.setOwnerAccount(accountBo);
        } else {
            approvalBizExchange.setMerchantAccount(accountBo);
        }
        final ApprovalApplyDto applyDto = frozenAmountIfNecessary(approvalBizExchange);

        Optional.ofNullable(applyDto.getTransactionId())
                .ifPresent(approvalBizExchange::setTransactionId);

        approvalBizExchangeRepository.saveAndFlush(approvalBizExchange);

        final ApprovalBizExchangeDto bizExchangeDto =
                modelMapper.convertApprovalBizExchange(approvalBizExchange);

        return CreateBizExchangeBo.builder().applyDto(applyDto).bizExchangeDto(bizExchangeDto)
                .build();
    }

    public Approval createdApprovalAndApprovalNode(
            final CreateApprovalBizExchangeVo vo,
            final LarkCreateApprovalInstancesDto instancesDto,
            final LocalDateTime now) {

        log.info("created approval and approvalNode instancesCode={}",
                instancesDto.getInstanceCode());

        final Approval approval = Approval.builder().type(ApprovalTypeEnum.EXCHANGE)
                .status(ApprovalStatusEnum.PROCESSING)
                .instanceCode(instancesDto.getInstanceCode())
                .revokeFlag(true).build();
        approvalRepository.saveAndFlush(approval);

        final ApprovalUserInfoBo userInfoBo = ApprovalUserInfoBo.builder()
                .userName(vo.getMerchantUserName()).build();

        final ApprovalNodeVo approvalNodeVo = ApprovalNodeVo.builder()
                .approvalId(approval.getId())
                .approvalInstanceCode(approval.getInstanceCode())
                .nodeId(UUID.randomUUID().toString())
                .nodeName(ApprovalNodeTypeEnum.START.getName())
                .nodeType(ApprovalNodeTypeEnum.START)
                .startTime(now)
                .endTime(now)
                .approvalNodeStatus(ApprovalNodeStatusEnum.APPROVED)
                .pendingApprovers(List.of(userInfoBo))
                .approvalOptionType(ApprovalOptionTypeEnum.AND)
                .doneApprovers(List.of(userInfoBo))
                .build();

        approvalNodeService.saveOrUpdate(approvalNodeVo);

        return approval;
    }


    private ApprovalApplyDto frozenAmountIfNecessary(
            final ApprovalBizExchange approvalBizExchange) {

        ApprovalApplyDto approvalApplyDto = new ApprovalApplyDto();
        if (ExchangeAccountTypeEnum.OFFLINE == approvalBizExchange.getExchangeAccountType()) {
            return approvalApplyDto;
        }

        if (ApprovalBizExchangeStatusEnum.PROCESSING != approvalBizExchange.getStatus()) {
            return approvalApplyDto;
        }

        final ResponseDto<ApprovalApplyDto> responseDto =
                statementFeign.approvalApply(ApprovalApplyVo.builder()
                        .bizTypeCode(BusinessTypeEnum.EXCHANGE)
                        .requestId(String.valueOf(SnowflakeIdUtil.generate()))
                        .merchantId(approvalBizExchange.getMerchantId())
                        .accountId(approvalBizExchange.getAccountId())
                        .amount(approvalBizExchange.getExchangeAmount()).build());

        CheckResponseUtil.checkResponseData(responseDto);

        log.info("apply frozen withdrawal amount transactionId-> {}",
                responseDto.getData().getTransactionId());
        return Optional.ofNullable(responseDto.getData()).stream()
                .filter(result -> Objects.nonNull(result.getTransactionId())).findFirst()
                .orElseThrow(
                        TransactionExceptionCode.APPLY_FROZEN_WITHDRAWAL_AMOUNT_ERROR::exception);
    }

    private ApprovalBizExchangeStatusEnum getExchangeStartStatus(
            final ApprovalBizExchange approvalBizExchange,
            final ApprovalBizExchangeConfigBo.SupportConfigBo config) {

        if (Boolean.TRUE.equals(config.getCustomExchangeRate())) {
            return ApprovalBizExchangeStatusEnum.WAIT_EXCHANGE_RATE;
        }

        if (ExchangeAccountTypeEnum.OFFLINE == approvalBizExchange.getExchangeAccountType()) {
            return ApprovalBizExchangeStatusEnum.WAIT_PROOF;
        }
        return ApprovalBizExchangeStatusEnum.PROCESSING;
    }

    @Override
    public void cancelApprovalBizExchange(final QueryApprovalBizExchangeVo vo,
                                          final boolean doActionFlag) {

        final String strBizExchangeId = String.valueOf(vo.getId());

        this.redisDistLock.checkRepeatRequest(
                Constant.CACHE.BIZ_EXCHANGE_CANCLE_LOCK, strBizExchangeId, 1, TimeUnit.DAYS);

        final String approvalCode = approvalExchangeProperties.getApprovalCode();

        try {
            final ApprovalBizExchange exchange =
                    this.findByIdAndMerchantId(vo.getId(), vo.getMerchantId());

            final Approval approval = exchange.getApproval();

            if (Boolean.FALSE.equals(approval.getRevokeFlag())
                    || ApprovalBizExchangeStatusEnum.CANCELED == exchange.getStatus()) {
                throw TransactionExceptionCode.CANCEL_APPROVAL_ERROR.exception();
            }
            final LarkApprovalInstancesVo approvalInstancesVo =
                    LarkApprovalInstancesVo.builder().approvalCode(approvalCode)
                            .instanceCode(approval.getInstanceCode()).build();
            larkApprovalService.cancelApprovalInstance(approvalInstancesVo);

            boolean approvalRollBack = false;
            if (doActionFlag && ApprovalBizExchangeStatusEnum.PROCESSING == exchange.getStatus()
                    && ExchangeAccountTypeEnum.OFFLINE != exchange.getExchangeAccountType()) {
                approvalRollBack = approvalRollBack(exchange);
            }
            if (approvalRollBack
                    || (ExchangeAccountTypeEnum.OFFLINE != exchange.getExchangeAccountType()
                    &&
                    ApprovalBizExchangeStatusEnum.WAIT_EXCHANGE_RATE == exchange.getStatus())) {
                exchange.setStatus(ApprovalBizExchangeStatusEnum.CANCELED);
            } else {
                exchange.setStatus(ApprovalBizExchangeStatusEnum.FAILED);
            }
            approvalBizExchangeRepository.save(exchange);
        } finally {
            redisDistLock.unlock(Constant.CACHE.BIZ_EXCHANGE_CANCLE_LOCK + strBizExchangeId,
                    strBizExchangeId);
        }

    }

    @Override
    public QueryApprovalBizExchangeDto queryApprovalBizExchange(
            final QueryApprovalBizExchangeVo vo) {

        final ApprovalBizExchange approvalBizExchange =
                this.findByIdAndMerchantId(vo.getId(), vo.getMerchantId());
        final List<AppendixDto> appendixes =
                ObjectUtils.isEmpty(Optional.ofNullable(approvalBizExchange.getApproval())
                        .map(Approval::getAppendixIds).orElse(null))
                        ? List.of() : appendixService.findAllByIds(
                        approvalBizExchange.getApproval().getAppendixIds());
        final QueryApprovalBizExchangeDto dto =
                modelMapper.convertQueryApprovalBizExchange(approvalBizExchange, appendixes);

        if (StringUtils.isNotBlank(approvalBizExchange.getSubMerchantId())) {
            dto.setSubMerchant(baseService.getSubMerchant(
                    vo.getMerchantId(), approvalBizExchange.getSubMerchantId()));
        }

        return dto;
    }

    @Override
    public PageVo<ApprovalBizExchangeDto> pageApprovalBizExchange(
            final PageApprovalBizExchangeVo vo) {

        final Specification<ApprovalBizExchange> spec =
                Specifications.<ApprovalBizExchange>and()
                        .in(CollectionUtils.isNotEmpty(vo.getExchangeAccountTypeList()),
                                "exchangeAccountType", vo.getExchangeAccountTypeList())
                        .eq(Objects.nonNull(vo.getMerchantId()), "merchantId",
                                vo.getMerchantId())
                        .eq(Objects.nonNull(vo.getMerchantCode()), "merchantCode",
                                vo.getMerchantCode())
                        .eq(Objects.nonNull(vo.getSubMerchantId()), "subMerchantId",
                                vo.getSubMerchantId())
                        .like(Objects.nonNull(vo.getMerchantName()), "merchantName",
                                String.format("%s%%", vo.getMerchantName()))
                        .eq(Objects.nonNull(vo.getCountryCode()), "countryCode",
                                vo.getCountryCode())
                        .eq(Objects.nonNull(vo.getCurrency()), "exchangeCurrency",
                                vo.getCurrency())
                        .ne("status", ApprovalBizExchangeStatusEnum.CANCELED)
                        .in(ObjectUtils.isNotEmpty(vo.getStatusList()), "status",
                                vo.getStatusList())
                        .ge(Objects.nonNull(vo.getStartDate()), "createdTime",
                                (vo.getStartDate()))
                        .le(Objects.nonNull(vo.getEndDate()), "createdTime", (vo.getEndDate()))
                        .build();

        final Page<ApprovalBizExchange> page = approvalBizExchangeRepository.findAll(spec,
                PageRequest.of(vo.getPageNo() - 1, vo.getPageSize(),
                        Sort.by(Sort.Order.desc("createdTime"))),
                EntityGraphUtils.fromAttributePaths("approval"));

        final List<ApprovalBizExchangeDto> dtoList =
                modelMapper.convertApprovalBizExchangeList(page.getContent());

        final Set<String> subMerchantIds = dtoList.stream()
                .map(ApprovalBizExchangeDto::getSubMerchantId).collect(Collectors.toSet());
        final Map<String, SubMerchantDto> subMerchantMap =
                baseService.getSubMerchant(vo.getMerchantId(), subMerchantIds).stream()
                        .collect(Collectors.toMap(SubMerchantDto::getSubMerchantId, v -> v));

        dtoList.forEach(v -> {
            if (Objects.nonNull(v.getSubMerchantId())
                    && Objects.nonNull(subMerchantMap.get(v.getSubMerchantId()))) {
                v.setSubMerchantName(subMerchantMap.get(v.getSubMerchantId()).getCommercialName());
            }
        });

        final Map<Long, List<ApprovalNodeSimpleDto>> nodeMap = approvalNodeService.list(
                        dtoList.stream().map(ApprovalBizExchangeDto::getApprovalId)
                                .collect(Collectors.toList()))
                .stream()
                .collect(Collectors.groupingBy(ApprovalNodeSimpleDto::getApprovalId));

        dtoList.forEach(v -> v.setNodes(nodeMap.get(v.getApprovalId())));

        return new PageVo<>(vo.getPageNo(), vo.getPageSize(), page.getTotalElements(), dtoList);
    }

    private boolean approvalRollBack(final ApprovalBizExchange exchange) {

        final ResponseDto<Void> responseDto = statementFeign.approvalRollBack(
                ApprovalRollBackVo.builder()
                        .bizTypeCode(BusinessTypeEnum.EXCHANGE)
                        .transactionId(Optional.ofNullable(exchange.getTransactionId())
                                .orElse(exchange.getId()))
                        .requestId(String.valueOf(SnowflakeIdUtil.generate()))
                        .merchantId(exchange.getMerchantId())
                        .accountId(exchange.getAccountId())
                        .amount(exchange.getExchangeAmount())
                        .build());
        if (Objects.isNull(responseDto) || responseDto.isFail()) {
            return false;
        }
        return responseDto.isSuccess();
    }

    @Override
    public void contractConfirm(final Long exchangeApprovalId) {

        final ApprovalBizExchange exchange = this.findById(exchangeApprovalId);

        if (ApprovalBizExchangeStatusEnum.WAIT_CONFIRM_CONTRACT != exchange.getStatus()) {
            throw TransactionExceptionCode.REPEAT_CONFIRM_CONTRACT.exception();
        }

        exchange.setStatus(exchange.getExchangeAccountType() != ExchangeAccountTypeEnum.OFFLINE
                ? ApprovalBizExchangeStatusEnum.PROCESSING :
                ApprovalBizExchangeStatusEnum.WAIT_PROOF);
        exchange.setContractConfirmFlag(true);

        final ApprovalApplyDto applyDto = frozenAmountIfNecessary(exchange);
        Optional.ofNullable(applyDto.getTransactionId()).ifPresent(exchange::setTransactionId);

        // modify lark form the Deduction Amount、Receipt Amount(USD)
        final ApprovalBizExchangeForm form =
                modelMapper.convertCreateApprovalBizExchangeVo(exchange);
        final Map<String, String> formMap = approvalExchangeProperties.getFormMap().stream()
                .collect(Collectors.toMap(LarkProperties.ColumnBo::getColumn,
                        LarkProperties.ColumnBo::getLarkColumn));

        larkApprovalService.agreeApprovalInstanceNode(AgreeApprovalInstancesNodeVo.builder()
                .approvalCode(approvalExchangeProperties.getApprovalCode())
                .instanceCode(exchange.getApproval().getInstanceCode())
                .formMap(formMap)
                .formData(form)
                .build());

        approvalBizExchangeRepository.save(exchange);
    }

    @Override
    public CalculateExchangeAmountDto calculateExchangeAmount(
            final CalculateExchangeAmountVo vo) {
        // get local currency
        final ResponseDto<AccountDto> accountDtoResponseDto = statementFeign
                .queryAccountInfo(QueryAccountInfoVo.builder().accountId(vo.getAccountId())
                        .merchantId(vo.getMerchantId()).build());

        CheckResponseUtil.checkResponse(accountDtoResponseDto);

        final AccountDto accountDto = accountDtoResponseDto.getData();

        if (vo.getTargetCurrency() == (accountDto.getCurrency())) {
            throw TransactionExceptionCode.APPLY_EXCHANGE_CURRENCY_ERROR.exception();
        }

        final MerchantRatioBo merchantRateBo =
                this.getMerchantRatio(vo.getMerchantId(), vo.getAccountId(), vo.getSourceCurrency(),
                        vo.getTargetCurrency());

        final BigDecimal merchantRate = merchantRateBo.getMerchantRate();

        final CalculateExchangeAmountDto result = CalculateExchangeAmountDto.builder()
                .merchantRate(merchantRate)
                .exchangeRate(merchantRateBo.getExchangeRate())
                .ratioLose(merchantRateBo.getRatioLose())
                .deductionCurrencyCode(vo.getSourceCurrency())
                .receiptCurrencyCode(vo.getTargetCurrency())
                .requestedAmount(vo.getAmount())
                .requestedCurrencyCode(vo.getCurrencyCode())
                .customFee(vo.getCustomFee())
                .build();

        if (vo.getCurrencyCode() != accountDto.getCurrency()) {
            result.setDeductionAmount(vo.getAmount().multiply(merchantRate));
            result.setReceiptAmount(vo.getAmount());

            result.setRequestedAmountSourceCurrency(result.getDeductionAmount());
            result.setRequestedAmountTargetCurrency(vo.getAmount());
        } else {
            result.setDeductionAmount(vo.getAmount());
            result.setReceiptAmount(vo.getAmount().divide(merchantRate, RoundingMode.HALF_UP));

            result.setRequestedAmountSourceCurrency(vo.getAmount());
            result.setRequestedAmountTargetCurrency(result.getReceiptAmount());
        }

        this.calculateFee(vo, result);

        return result;
    }

    private void calculateFee(final CalculateExchangeAmountVo vo,
                              final CalculateExchangeAmountDto result) {

        final ApprovalBizExchangeConfigBo.SupportConfigBo config =
                this.queryApprovalConfig(vo.getAccountId()).getConfig().getSupportConfig()
                        .get(vo.getTargetCurrency());

        updateConfigFeeIfCustomFee(vo, config);

        final ApprovalBizExchangeConfigBo.ExchangeFeeBo exchangeFee = config.getExchangeFee();

        if (!exchangeFee.getOpenFlag()) {
            result.setFeeFlag(false);

            result.setSmallFee(BigDecimal.ZERO);
            result.setSmallFeeSourceCurrency(BigDecimal.ZERO);
            result.setSmallFeeTargetCurrency(BigDecimal.ZERO);

            result.setSmallFeeLimit(BigDecimal.ZERO);
            result.setSmallFeeLimitSourceCurrency(BigDecimal.ZERO);
            result.setSmallFeeLimitTargetCurrency(BigDecimal.ZERO);

            result.setFeeAmount(BigDecimal.ZERO);
            result.setFeeAmountSourceCurrency(BigDecimal.ZERO);
            result.setFeeAmountTargetCurrency(BigDecimal.ZERO);

            result.setFeeTotalAmount(BigDecimal.ZERO);
            result.setFeeTotalAmountSourceCurrency(BigDecimal.ZERO);
            result.setFeeTotalAmountTargetCurrency(BigDecimal.ZERO);

            result.setSmallFeeCurrencyCode(vo.getTargetCurrency());
            result.setFeeCurrencyCode(vo.getTargetCurrency());

            return;
        }

        result.setFeeFlag(true);
        result.setFeeCurrencyCode(exchangeFee.getCurrency());
        result.setFeeMode(exchangeFee.getFeeMode());
        result.setFeeValue(exchangeFee.getFeeValue());
        result.setSmallFeeCurrencyCode(exchangeFee.getCurrency());
        result.setSmallFeeLimit(exchangeFee.getSmallFeeLimit());

        final BigDecimal smallFee = this.calculateSmallFee(result, config);
        result.setSmallFee(smallFee);

        if (result.getDeductionCurrencyCode() != exchangeFee.getCurrency()) {

            result.setFeeAmount((FeeModeEnum.PERCENTAGE == exchangeFee.getFeeMode()
                    ? result.getReceiptAmount().multiply(exchangeFee.getFeeValue()) :
                    exchangeFee.getFeeValue()));
            result.setFeeAmountSourceCurrency(
                    result.getFeeAmount().multiply(result.getMerchantRate()));
            result.setFeeAmountTargetCurrency(result.getFeeAmount());

            result.setSmallFeeSourceCurrency(
                    result.getSmallFee().multiply(result.getMerchantRate()));
            result.setSmallFeeTargetCurrency(result.getSmallFee());

            result.setSmallFeeLimitSourceCurrency(
                    result.getSmallFeeLimit().multiply(result.getMerchantRate()));
            result.setSmallFeeLimitTargetCurrency(result.getSmallFeeLimit());

            result.setFeeTotalAmount(result.getSmallFee().add(result.getFeeAmount()));
            result.setFeeTotalAmountSourceCurrency(
                    result.getFeeTotalAmount().multiply(result.getMerchantRate()));
            result.setFeeTotalAmountTargetCurrency(result.getFeeTotalAmount());

        } else {

            result.setFeeAmount((FeeModeEnum.PERCENTAGE == exchangeFee.getFeeMode()
                    ? result.getDeductionAmount().multiply(exchangeFee.getFeeValue()) :
                    exchangeFee.getFeeValue()));
            result.setFeeAmountSourceCurrency(result.getFeeAmount());
            result.setFeeAmountTargetCurrency(
                    result.getFeeAmount()
                            .divide(result.getMerchantRate(), RoundingMode.HALF_UP));

            result.setSmallFeeSourceCurrency(result.getSmallFee());
            result.setSmallFeeTargetCurrency(
                    result.getSmallFee()
                            .divide(result.getMerchantRate(), RoundingMode.HALF_UP));

            result.setSmallFeeLimitSourceCurrency(result.getSmallFeeLimit());
            result.setSmallFeeLimitTargetCurrency(result.getSmallFeeLimit()
                    .divide(result.getMerchantRate(), RoundingMode.HALF_UP));

            result.setFeeTotalAmount(result.getSmallFee().add(result.getFeeAmount()));
            result.setFeeTotalAmountSourceCurrency(result.getFeeTotalAmount());
            result.setFeeTotalAmountTargetCurrency(result.getFeeTotalAmount()
                    .divide(result.getMerchantRate(), RoundingMode.HALF_UP));
        }

        result.setDeductionAmount(
                result.getDeductionAmount().add(result.getFeeTotalAmountSourceCurrency()));

        if (result.getReceiptAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw TransactionExceptionCode.APPLY_APPROVAL_AMOUNT_ERROR.exception("exchange");
        }
    }

    private void updateConfigFeeIfCustomFee(
            final CalculateExchangeAmountVo vo,
            final ApprovalBizExchangeConfigBo.SupportConfigBo config) {

        if (!vo.getCustomFee()) {
            return;
        }

        if (Objects.isNull(vo.getFee())
                || Objects.isNull(vo.getFeeCurrencyCode())
                || Objects.isNull(vo.getSmallFee())) {
            throw CommonExceptionCode.PARAMETER_ILLEGAL_NULL.exception();
        }

        final ApprovalBizExchangeConfigBo.ExchangeFeeBo exchangeFee = config.getExchangeFee();

        exchangeFee.setOpenFlag(true);
        exchangeFee.setFeeMode(FeeModeEnum.FIXED);
        exchangeFee.setFeeValue(vo.getFee());
        exchangeFee.setCurrency(vo.getFeeCurrencyCode());
        exchangeFee.setSmallFee(vo.getSmallFee());
        exchangeFee.setSmallFeeLimit(new BigDecimal(Long.MAX_VALUE));

    }

    private MerchantRatioBo getMerchantRatio(final Long merchantId, final Long accountId,
                                             final CurrencyEnum exchangeSourceCurrency,
                                             final CurrencyEnum exchangeTargetCurrency) {
        // get exchange rate
        final ResponseDto<RealTimeExchangeRateDto> responseDto =
                statementFeign.queryLatestExchangeRate(
                        QueryLatestExchangeRateVo.builder()
                                .merchantId(merchantId)
                                .accountId(accountId)
                                .sourceCurrency(exchangeTargetCurrency)
                                .targetCurrency(exchangeSourceCurrency).build());

        CheckResponseUtil.checkResponse(responseDto);

        // get lose rate
        QueryApprovalConfigDto<ApprovalBizExchangeConfigBo> configDto =
                this.queryApprovalConfig(accountId);
        if (Objects.isNull(configDto)
                || Objects.isNull(configDto.getConfig())
                || Objects.isNull(configDto.getConfig().getSupportConfig())
                || Objects.isNull(configDto.getConfig().getSupportConfig()
                .get(exchangeTargetCurrency))
                || Objects.isNull(configDto.getConfig().getSupportConfig()
                .get(exchangeTargetCurrency).getRatioLose())) {
            throw TransactionExceptionCode.APPROVAL_CONFIG_ERROR.exception();
        }

        final ApprovalBizExchangeConfigBo config = configDto.getConfig();

        final BigDecimal exchangeRate = responseDto.getData().getExchangeRate();
        final BigDecimal ratioLose =
                config.getSupportConfig().get(exchangeTargetCurrency).getRatioLose();
        final BigDecimal merchantRate = exchangeRate.multiply(BigDecimal.ONE.add(ratioLose))
                .setScale(6, RoundingMode.HALF_UP);

        log.info("accountId {} exchangeRate {} ratioLose {} merchantRate {}", accountId,
                exchangeRate, ratioLose, merchantRate);
        return MerchantRatioBo.builder()
                .sourceCurrency(exchangeTargetCurrency)
                .targetCurrency(exchangeSourceCurrency)
                .exchangeRate(exchangeRate)
                .ratioLose(ratioLose)
                .merchantRate(merchantRate)
                .build();
    }

    @Async
    @TransactionalEventListener
    public void finishApprovalBizExchangeHandle(final ApprovalFinishEvent event) {

        final ApprovalFinishEventBo args = event.getEventArgs();
        if (ApprovalTypeEnum.EXCHANGE != args.getApprovalType()) {
            return;
        }

        log.info("finishApprovalBizExchangeHandle request param={}", event);
        final ApprovalBizExchange exchange = this.findByApprovalId(args.getApprovalId());

        if (ApprovalBizExchangeStatusEnum.COMPLETED != exchange.getStatus()) {
            return;
        }
        publisher.publishEvent(
                new ApprovalBizExchangeFinishEvent(ApprovalBizExchangeFinishEventBo.builder()
                        .exchange(exchange)
                        .approvalStatus(args.getApprovalStatus())
                        .executeResult(args.getExecuteResult())
                        .message(args.getMessage())
                        .build()));
    }

    @Transactional
    @Override
    public List<QueryApprovalConfigDto<ApprovalBizExchangeConfigBo>> createApprovalConfigBatch(
            final List<CreateApprovalConfigVo<ApprovalBizExchangeConfigBo>> list) {

        List<ApprovalConfig> collect = list.stream()
                .map(this::createApprovalConfig)
                .collect(Collectors.toList());
        approvalConfigRepository.saveAll(collect);

        List<QueryApprovalConfigDto<ApprovalBizExchangeConfigBo>> result = new ArrayList<>();
        collect.forEach(item -> result.add(modelMapper.convert(item)));

        return result;
    }

    private ApprovalConfig createApprovalConfig(
            final CreateApprovalConfigVo<ApprovalBizExchangeConfigBo> vo) {

        final ApprovalConfig dbConfig =
                approvalConfigRepository.findByAccountIdAndApprovalType(vo.getAccountId(),
                        ApprovalTypeEnum.EXCHANGE);
        if (Objects.nonNull(dbConfig)) {
            throw TransactionExceptionCode.APPROVAL_CONFIG_REPETITION.exception(
                    dbConfig.getId());
        }

        final ResponseDto<AccountDto> account =
                statementFeign.getEffectiveAccountInfo(
                        QueryAccountVo.builder().id(vo.getAccountId()).build());
        CheckResponseUtil.checkResponseData(account);

        final ApprovalBizExchangeConfigBo allConfig = vo.getConfig();
        allConfig.setOverWithdrawAmountFlag(
                Objects.nonNull(allConfig.getOverWithdrawAmountFlag())
                        ? allConfig.getOverWithdrawAmountFlag() : false);
        allConfig.getSupportConfig().values().forEach(config -> {
            config.setRatioLose(
                    Objects.nonNull(config.getRatioLose()) ? config.getRatioLose() :
                            BigDecimal.ZERO);
            config.setCustomExchangeRate(
                    Objects.nonNull(config.getCustomExchangeRate())
                            ? config.getCustomExchangeRate() : false);
            config.setNeedConfirmContract(
                    Objects.nonNull(config.getNeedConfirmContract())
                            ? config.getNeedConfirmContract() : true);

            final AtomicLong merchantAccountId = new AtomicLong(0);
            config.getMerchantAccountInfo()
                    .forEach(accountInfo -> accountInfo.setId(
                            merchantAccountId.incrementAndGet()));

            config.setOwnerAccountInfo(Objects.nonNull(config.getOwnerAccountInfo())
                    ? config.getOwnerAccountInfo() : List.of());

            final AtomicLong ownerAccountId = new AtomicLong(0);
            config.getOwnerAccountInfo()
                    .forEach(accountInfo -> accountInfo.setId(ownerAccountId.incrementAndGet()));
            config.setCostConfig(List.of());
        });

        final LocalDateTime nowUtc = LocalDateTimeUtil.nowUtc();
        return ApprovalConfig.builder()
                .merchantId(account.getData().getMerchantId())
                .accountId(vo.getAccountId())
                .config(vo.getConfig())
                .configText(JsonUtil.toJson(allConfig))
                .approvalType(ApprovalTypeEnum.EXCHANGE)
                .mailRecipient(vo.getMailRecipient())
                .larkNotice(CollectionUtils.isNotEmpty(vo.getLarkNotice())
                        ? BeanCopierUtil.copyPropertyList(vo.getLarkNotice(),
                        ApprovalConfig.LarkNoticeItem.class) : List.of())
                .createdTime(nowUtc)
                .updatedTime(nowUtc)
                .build();
    }

}
