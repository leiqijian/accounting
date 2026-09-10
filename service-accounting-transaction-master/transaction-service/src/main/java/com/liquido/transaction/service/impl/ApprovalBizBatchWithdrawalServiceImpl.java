package com.liquido.transaction.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.liquido.base.enums.BusinessTypeEnum;
import com.liquido.base.enums.DownloadFileTypeEnum;
import com.liquido.core.common.exception.CommonExceptionCode;
import com.liquido.core.common.snowflake.SnowflakeIdUtil;
import com.liquido.core.common.utils.AmountUtil;
import com.liquido.core.common.utils.CheckResponseUtil;
import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.core.mvc.vo.PageVo;
import com.liquido.statement.StatementApis;
import com.liquido.statement.pojo.dto.AccountDto;
import com.liquido.statement.pojo.dto.BatchWithdrawalApplyDto;
import com.liquido.statement.pojo.dto.DealershipWithdrawalInfoDto;
import com.liquido.statement.pojo.vo.ApprovalBatchApplyItemVo;
import com.liquido.statement.pojo.vo.ApprovalBatchApplyVo;
import com.liquido.statement.pojo.vo.BatchWithdrawalApplyVo;
import com.liquido.statement.pojo.vo.BatchWithdrawalApprovedHandleDetailVo;
import com.liquido.statement.pojo.vo.BatchWithdrawalApprovedHandleVo;
import com.liquido.statement.pojo.vo.ListBatchWithdrawalApplyVo;
import com.liquido.statement.pojo.vo.QueryAccountVo;
import com.liquido.transaction.common.properties.LarkProperties;
import com.liquido.transaction.enums.ApprovalBizBatchWithdrawalStatusEnum;
import com.liquido.transaction.enums.ApprovalNodeStatusEnum;
import com.liquido.transaction.enums.ApprovalNodeTypeEnum;
import com.liquido.transaction.enums.ApprovalOptionTypeEnum;
import com.liquido.transaction.enums.ApprovalStatusEnum;
import com.liquido.transaction.enums.ApprovalTypeEnum;
import com.liquido.transaction.exception.TransactionExceptionCode;
import com.liquido.transaction.pojo.bo.ApprovalUserInfoBo;
import com.liquido.transaction.pojo.bo.BatchWithdrawalExcelDataBo;
import com.liquido.transaction.pojo.bo.PreProcessBizBatchWithdrawalBo;
import com.liquido.transaction.pojo.dto.AppendixDto;
import com.liquido.transaction.pojo.dto.ApprovalBizBatchWithdrawalDto;
import com.liquido.transaction.pojo.dto.ApprovalNodeSimpleDto;
import com.liquido.transaction.pojo.dto.LarkCreateApprovalInstancesDto;
import com.liquido.transaction.pojo.dto.PageApprovalBizBatchWithdrawalDto;
import com.liquido.transaction.pojo.entity.Approval;
import com.liquido.transaction.pojo.entity.ApprovalBizBatchWithdrawal;
import com.liquido.transaction.pojo.form.ApprovalBizBatchWithdrawalForm;
import com.liquido.transaction.pojo.mapper.ModelMapper;
import com.liquido.transaction.pojo.vo.ApprovalNodeVo;
import com.liquido.transaction.pojo.vo.CreateApprovalBizBatchWithdrawalVo;
import com.liquido.transaction.pojo.vo.CreateBatchWithdrawalApprovalVo;
import com.liquido.transaction.pojo.vo.LarkApprovalInstancesVo;
import com.liquido.transaction.pojo.vo.PageApprovalBizBatchWithdrawalVo;
import com.liquido.transaction.pojo.vo.QueryApprovalBizBatchWithdrawalVo;
import com.liquido.transaction.repository.ApprovalBizBatchWithdrawalRepository;
import com.liquido.transaction.repository.ApprovalRepository;
import com.liquido.transaction.service.ApprovalBizBatchWithdrawalService;
import com.liquido.transaction.service.ApprovalNodeService;
import com.liquido.transaction.service.ApprovalService;
import com.liquido.transaction.service.LarkApprovalService;
import com.liquido.transaction.service.monitor.LarkRobotMonitor;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.cosium.spring.data.jpa.entity.graph.domain.EntityGraphUtils;
import com.github.wenhao.jpa.Specifications;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApprovalBizBatchWithdrawalServiceImpl implements ApprovalBizBatchWithdrawalService {

    private static final String TEMPLATE_PATH = "templates/batch-daily-withdrawal-template.xlsx";

    private final ModelMapper modelMapper;
    private final ApprovalService approvalService;
    private final ApprovalRepository approvalRepository;
    private final LarkRobotMonitor larkRobotMonitor;
    private final LarkApprovalService larkApprovalService;
    private final ApprovalNodeService approvalNodeService;
    private final StatementApis.StatementFeign statementFeign;
    private final LarkProperties.ApprovalBatchWithdrawal approvalBatchWithdrawalProperties;
    private final ApprovalBizBatchWithdrawalRepository approvalBizBatchWithdrawalRepository;

    @Override
    public ApprovalBizBatchWithdrawalDto createApprovalBizBatchWithdrawal(
            final CreateBatchWithdrawalApprovalVo vo) {
        final AccountDto accountDto = CheckResponseUtil.checkAndReturnResponseData(
                statementFeign.getEffectiveAccountInfo(
                        QueryAccountVo.builder().id(vo.getAccountId()).build()));
        try {
            final ResponseDto<BatchWithdrawalApplyDto> responseDto =
                    statementFeign.batchWithdrawalApply(BatchWithdrawalApplyVo.builder()
                            .merchantId(accountDto.getMerchantId()).accountId(accountDto.getId())
                            .creditedDate(vo.getCreditedDate())
                            .build());
            final BatchWithdrawalApplyDto batchWithdrawalApplyDto =
                    CheckResponseUtil.checkAndReturnResponseData(responseDto);
            if (batchWithdrawalApplyDto.getDataList().isEmpty()) {
                log.info("daily batch withdrawal data is empty, not need to create approval.");
                throw TransactionExceptionCode.CREATE_APPROVAL_ERROR.exception("no data");
            }

            final CreateApprovalBizBatchWithdrawalVo createVo =
                    modelMapper.convert(batchWithdrawalApplyDto);
            createVo.setInitiator("INTERFACE");

            return this.createApprovalBizBatchWithdrawal(createVo);
        } catch (Exception e) {
            log.error("accountId: {} build batch withdrawal failed, {}",
                    vo.getAccountId(), e.getMessage());
            throw TransactionExceptionCode.CREATE_APPROVAL_ERROR.exception(e.getMessage());
        }
    }

    @Override
    public ApprovalBizBatchWithdrawalDto createApprovalBizBatchWithdrawal(
            final CreateApprovalBizBatchWithdrawalVo vo) {

        final LocalDateTime now = LocalDateTimeUtil.nowUtc().minusSeconds(5);
        vo.setApprovalBizBatchWithdrawalId(SnowflakeIdUtil.generate());
        log.info("start creating approval by vo: {}", vo);
        boolean hasFrozenAmount = false;
        LarkCreateApprovalInstancesDto dto = new LarkCreateApprovalInstancesDto();

        try {
            // step 1: pre-process
            final PreProcessBizBatchWithdrawalBo bo = preProcessBizBatchWithdrawal(vo);

            // step 2: apply frozen withdrawal amount
            hasFrozenAmount = applyFrozenAmountBeforeCreateApproval(vo);

            // step 3: create approval instance */
            dto = createApprovalBizBatchWithdrawalInstance(vo, bo);

            // step 4: create approval and approval node */
            final Approval approval = createApprovalAndApprovalNode(vo, dto, bo, now);

            // step 5: create biz */
            return createApprovalAndBizBatchWithdrawal(vo, approval);

        } catch (Exception e) {
            log.error("create batch withdrawal failed: {}", e.getMessage(), e);
            createApprovalFailedPostProcess(vo, hasFrozenAmount, dto, e.getMessage());
            throw TransactionExceptionCode.LARK_APPROVAL_ERROR.exception(e.getMessage());
        }
    }

    private void createApprovalFailedPostProcess(final CreateApprovalBizBatchWithdrawalVo vo,
                                                 final boolean hasFrozenAmount,
                                                 final LarkCreateApprovalInstancesDto dto,
                                                 final String errorMessage) {
        try {
            if (hasFrozenAmount) {
                final List<BatchWithdrawalApprovedHandleDetailVo> rejectedList =
                        vo.getDataList().stream().map(v -> {
                            v.setState(3);
                            return modelMapper.convertBatchWithdrawalApprovedHandleDetailVo(v);
                        }).collect(Collectors.toList());
                final ResponseDto<Void> responseDto = statementFeign.batchWithdrawalApprovedHandle(
                        BatchWithdrawalApprovedHandleVo.builder()
                                .accountId(vo.getAccountId())
                                .requestId(String.valueOf(SnowflakeIdUtil.generate()))
                                .batchId(vo.getBatchId())
                                .unFrozenAmount(vo.getWithdrawalAmount())
                                .settlementCurrency(vo.getSettlementCurrency())
                                .approvalApprovedAmount(BigDecimal.ZERO)
                                .approvalRejectedAmount(vo.getWithdrawalAmount())
                                .approvalApprovedCount(0)
                                .approvalApprovedList(Lists.newArrayList())
                                .approvalRejectedList(rejectedList)
                                .build());
                CheckResponseUtil.checkResponse(responseDto);
            }
        } catch (Exception e) {
            log.error("created batch withdrawal fail post processing", e);
            larkRobotMonitor.error("Create Approval Batch Withdrawal Error",
                    "Batch Withdrawal Approval On Frozen Amount Roll Back Fail",
                    e.getMessage());
        } finally {
            if (StringUtils.isNotBlank(dto.getInstanceCode())) {
                larkApprovalService.cancelApprovalInstance(LarkApprovalInstancesVo.builder()
                        .approvalCode(approvalBatchWithdrawalProperties.getApprovalCode())
                        .instanceCode(dto.getInstanceCode()).build());
            }

            final String sb = "**Merchant:** "
                    + vo.getMerchantCode() + "\\n"
                    + "**Country :** "
                    + vo.getCountryCode().getCountryName() + "\\n"
                    + "**BatchId:** "
                    + vo.getBatchId() + "\\n"
                    + "**Amount:** "
                    + AmountUtil.centToYuan(vo.getWithdrawalAmount()) + "\\n"
                    + "**Currency:** "
                    + vo.getSettlementCurrency().getCode() + "\\n"
                    + "**ApplicationTime:** "
                    + LocalDateTimeUtil.nowUtcToLocal("UTC+8")
                    .format(LocalDateTimeUtil.FORMAT_DATETIME)
                    + "\\n";

            larkRobotMonitor.error("Create Approval Batch Withdrawal Error", sb,
                    errorMessage);
        }
    }

    private ApprovalBizBatchWithdrawalDto createApprovalAndBizBatchWithdrawal(
            final CreateApprovalBizBatchWithdrawalVo vo, final Approval approval) {
        final ApprovalBizBatchWithdrawal approvalBizBatchWithdrawal = modelMapper.convert(vo);
        approvalBizBatchWithdrawal.setApproval(approval);
        approvalBizBatchWithdrawal.setStatus(ApprovalBizBatchWithdrawalStatusEnum.PROCESSING);
        approvalBizBatchWithdrawalRepository.saveAndFlush(approvalBizBatchWithdrawal);
        return modelMapper.convert(approvalBizBatchWithdrawal);
    }

    private Approval createApprovalAndApprovalNode(final CreateApprovalBizBatchWithdrawalVo vo,
                                                   final LarkCreateApprovalInstancesDto dto,
                                                   final PreProcessBizBatchWithdrawalBo bo,
                                                   final LocalDateTime now) {

        log.info("create approval and approval node transferOut instances={}", dto);
        final ApprovalUserInfoBo userInfoBo = ApprovalUserInfoBo.builder()
                .userName(vo.getInitiator()).build();

        // Save to Approval and ApprovalNode
        final Approval approval = Approval.builder().type(ApprovalTypeEnum.TRANSFER_OUT)
                .status(ApprovalStatusEnum.PROCESSING).instanceCode(dto.getInstanceCode())
                .revokeFlag(true).build();

        approvalRepository.saveAndFlush(approval);

        approvalNodeService.saveOrUpdate(ApprovalNodeVo.builder()
                .approvalId(approval.getId())
                .approvalInstanceCode(approval.getInstanceCode())
                .nodeId(UUID.randomUUID().toString()).nodeName(ApprovalNodeTypeEnum.START.getName())
                .nodeType(ApprovalNodeTypeEnum.START).startTime(now).endTime(now)
                .approvalNodeStatus(ApprovalNodeStatusEnum.APPROVED)
                .pendingApprovers(List.of(userInfoBo))
                .appendixIds(List.of(bo.getAppendix().getId()))
                .approvalOptionType(ApprovalOptionTypeEnum.AND).doneApprovers(List.of(userInfoBo))
                .build());

        return approval;
    }

    private LarkCreateApprovalInstancesDto createApprovalBizBatchWithdrawalInstance(
            final CreateApprovalBizBatchWithdrawalVo vo, final PreProcessBizBatchWithdrawalBo bo) {

        final ApprovalBizBatchWithdrawalForm form = modelMapper.convert(vo, bo);

        final Map<String, String> formMap = approvalBatchWithdrawalProperties.getFormMap().stream()
                .collect(Collectors.toMap(LarkProperties.ColumnBo::getColumn,
                        LarkProperties.ColumnBo::getLarkColumn));

        return larkApprovalService.createApprovalInstance(
                approvalBatchWithdrawalProperties.getApprovalCode(), formMap, form);
    }

    private boolean applyFrozenAmountBeforeCreateApproval(
            final CreateApprovalBizBatchWithdrawalVo vo) {

        log.info("batch withdrawal apply frozen amount merchantId={},accountId={},amount={}",
                vo.getMerchantId(), vo.getAccountId(), vo.getWithdrawalAmount());

        final List<ApprovalBatchApplyItemVo> dataList = vo.getDataList().stream()
                .map(v -> ApprovalBatchApplyItemVo.builder()
                        .transactionId(v.getId())
                        .amount(v.getWithdrawalAmount())
                        .subMerchantId(v.getSubMerchantId())
                        .build()).collect(Collectors.toList());

        final ResponseDto<Void> responseDto =
                statementFeign.approvalBatchApply(ApprovalBatchApplyVo.builder()
                        .bizTypeCode(BusinessTypeEnum.TRANSFER_OUT)
                        .accountId(vo.getAccountId())
                        .requestId(String.valueOf(SnowflakeIdUtil.generate()))
                        .batchId(vo.getBatchId())
                        .merchantId(vo.getMerchantId())
                        .totalAmount(vo.getWithdrawalAmount())
                        .dataList(dataList)
                        .build());

        CheckResponseUtil.checkResponse(responseDto);

        return responseDto.isSuccess();
    }

    private PreProcessBizBatchWithdrawalBo preProcessBizBatchWithdrawal(
            final CreateApprovalBizBatchWithdrawalVo vo) throws IOException {

        final List<DealershipWithdrawalInfoDto> dataList = vo.getDataList();

        // build attachments
        final byte[] fileBytes = buildAttachmentsFile(
                modelMapper.convertBatchWithdrawalExcelDataBo(dataList));

        final AppendixDto appendixDto =
                uploadFileToLarkApproval(fileBytes, "WithdrawalDetail.xlsx", vo.getMerchantId());

        return PreProcessBizBatchWithdrawalBo.builder()
                .appendix(appendixDto)
                .build();
    }

    private byte[] buildAttachmentsFile(final List<BatchWithdrawalExcelDataBo> dataList) {
        final WriteSheet writeSheet = EasyExcel.writerSheet("Sheet1").build();
        try (final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             final ExcelWriter excelWriter = EasyExcel.write(outputStream)
                     .withTemplate(new ClassPathResource(TEMPLATE_PATH).getInputStream())
                     .build()) {
            excelWriter.fill(dataList, writeSheet);
            excelWriter.finish();

            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("build multipart file failed: {}", e.getMessage(), e);
            throw TransactionExceptionCode.FILE_GENERATE_FAILED.exception();
        }
    }

    private AppendixDto uploadFileToLarkApproval(
            final byte[] content, final String fileName, final Long merchantId) throws IOException {

        final DownloadFileTypeEnum typeEnum = Arrays.stream(DownloadFileTypeEnum.values())
                .filter(v -> v.getExtension().equals(fileName.split("\\.")[1]))
                .findFirst()
                .orElseThrow(TransactionExceptionCode.LARK_APPROVAL_ERROR::exception);

        final FileItem fileItem = new DiskFileItem("content",
                typeEnum.getFileContentType(), true, fileName, content.length, null);

        try (OutputStream os = fileItem.getOutputStream()) {
            os.write(content);
        }

        final MultipartFile file = new CommonsMultipartFile(fileItem);

        return approvalService.uploadApprovalAppendix(file, merchantId);
    }

    @Override
    public ApprovalBizBatchWithdrawal findByApprovalId(final Long approvalId) {
        return approvalBizBatchWithdrawalRepository.findOne(
                        Specifications.<ApprovalBizBatchWithdrawal>and()
                                .eq("approval.id", approvalId).build(),
                        EntityGraphUtils.fromAttributePaths("approval"))
                .orElse(null);
    }

    @Override
    public ApprovalBizBatchWithdrawal findByBatchId(final Long batchId) {
        return approvalBizBatchWithdrawalRepository.findOne(
                        Specifications.<ApprovalBizBatchWithdrawal>and()
                                .eq("batchId", batchId).build(),
                        EntityGraphUtils.fromAttributePaths("approval"))
                .orElseThrow(CommonExceptionCode.DATA_NOT_FOUND::exception);
    }

    @Override
    public void validExcelBatchWithdrawalData(
            final List<BatchWithdrawalExcelDataBo> boList,
            final Map<String, DealershipWithdrawalInfoDto> dtoMap) {

        if (Objects.isNull(boList) || boList.isEmpty()) {
            throw TransactionExceptionCode.APPENDIX_DATA_ERROR.exception("boList is empty");
        }

        if (Objects.isNull(dtoMap) || dtoMap.isEmpty()) {
            throw TransactionExceptionCode.APPENDIX_DATA_ERROR.exception("dtoMap is empty");
        }

        if (boList.size() != dtoMap.size()) {
            throw TransactionExceptionCode.APPENDIX_DATA_ERROR.exception("size incorrect");
        }

        for (final BatchWithdrawalExcelDataBo bo : boList) {
            final DealershipWithdrawalInfoDto dto =
                    Optional.ofNullable(dtoMap.get(bo.getSubMerchantId())).orElseThrow(
                            () -> TransactionExceptionCode.APPENDIX_DATA_ERROR.exception(
                                    bo.getSubMerchantId() + "not found"));

            if (!dto.getId().toString().equals(bo.getTransactionId())) {
                throw TransactionExceptionCode.APPENDIX_DATA_ERROR.exception(
                        "id incorrect, id=" + dto.getId());
            }

            if (dto.getWithdrawalAmount().compareTo(
                    AmountUtil.yuanToCent(bo.getWithdrawalAmount())) != 0) {
                throw TransactionExceptionCode.APPENDIX_DATA_ERROR.exception(
                        "amount incorrect, id=" + dto.getId());
            }

            if (Optional.ofNullable(bo.getSettlementCurrency()).orElse("").trim()
                    .equalsIgnoreCase(dto.getSettlementCurrency().getCode())) {
                throw TransactionExceptionCode.APPENDIX_DATA_ERROR.exception(
                        "settlement currency incorrect, id=" + dto.getId());
            }
        }
    }

    @Override
    public BatchWithdrawalApprovedHandleVo parseExcelData(
            final ApprovalBizBatchWithdrawal batchWithdrawal,
            final List<BatchWithdrawalExcelDataBo> boList,
            final Map<String, DealershipWithdrawalInfoDto> dtoMap) {

        final List<BatchWithdrawalApprovedHandleDetailVo> successList = Lists.newArrayList();
        final List<BatchWithdrawalApprovedHandleDetailVo> rejectedList = Lists.newArrayList();
        BigDecimal unfrozenAmount = BigDecimal.ZERO;
        BigDecimal approvalApprovedAmount = BigDecimal.ZERO;
        BigDecimal approvalRejectedAmount = BigDecimal.ZERO;
        for (final BatchWithdrawalExcelDataBo bo : boList) {
            final DealershipWithdrawalInfoDto dto = dtoMap.get(bo.getSubMerchantId());
            unfrozenAmount = unfrozenAmount.add(dto.getWithdrawalAmount());
            if ("Success".equals(bo.getStatus())) {
                dto.setState(2);
                approvalApprovedAmount = approvalApprovedAmount.add(dto.getWithdrawalAmount());
                successList.add(modelMapper.convertBatchWithdrawalApprovedHandleDetailVo(dto));
            } else {
                dto.setState(3);
                approvalRejectedAmount = approvalRejectedAmount.add(dto.getWithdrawalAmount());
                rejectedList.add(modelMapper.convertBatchWithdrawalApprovedHandleDetailVo(dto));
            }
        }
        if (!(unfrozenAmount.compareTo(
                approvalApprovedAmount.add(approvalRejectedAmount)) == 0)) {
            log.error("The frozen amount is different from the "
                    + "total amount of success and failure.");
            throw TransactionExceptionCode.APPENDIX_DATA_ERROR.exception(
                    "The frozen amount is different from the "
                            + "total amount of success and failure.");
        }
        return BatchWithdrawalApprovedHandleVo.builder()
                .accountId(batchWithdrawal.getAccountId())
                .requestId(String.valueOf(SnowflakeIdUtil.generate()))
                .unFrozenAmount(unfrozenAmount)
                .batchId(batchWithdrawal.getBatchId())
                .approvalApprovedAmount(approvalApprovedAmount)
                .approvalRejectedAmount(approvalRejectedAmount)
                .settlementCurrency(batchWithdrawal.getSettlementCurrency())
                .approvalApprovedCount(successList.size())
                .approvalApprovedList(successList)
                .approvalRejectedList(rejectedList)
                .build();
    }

    @Override
    public ApprovalBizBatchWithdrawalDto queryApprovalBizBatchWithdrawal(
            final QueryApprovalBizBatchWithdrawalVo vo) {

        final ApprovalBizBatchWithdrawal batchWithdrawal =
                approvalBizBatchWithdrawalRepository.findOne(
                        Specifications.<ApprovalBizBatchWithdrawal>and()
                                .eq(Objects.nonNull(vo.getId()), "id", vo.getId())
                                .eq(Objects.nonNull(vo.getBatchId()), "batchId", vo.getBatchId())
                                .build(),
                        EntityGraphUtils.fromAttributePaths("approval")).orElse(null);

        if (Objects.isNull(batchWithdrawal)) {
            return null;
        }

        final List<DealershipWithdrawalInfoDto> dataList =
                CheckResponseUtil.checkAndReturnResponseData(
                        statementFeign.listWithdrawalApply(ListBatchWithdrawalApplyVo.builder()
                                .batchId(batchWithdrawal.getBatchId()).build()));

        final ApprovalBizBatchWithdrawalDto dto = modelMapper.convert(batchWithdrawal);
        dto.setDataList(dataList);

        return dto;
    }

    @Override
    public PageVo<PageApprovalBizBatchWithdrawalDto> pageApprovalBizBatchWithdrawal(
            final PageApprovalBizBatchWithdrawalVo vo) {

        final Specification<ApprovalBizBatchWithdrawal> spec =
                Specifications.<ApprovalBizBatchWithdrawal>and()
                        .eq(Objects.nonNull(vo.getMerchantId()), "merchantId", vo.getMerchantId())
                        .eq(Objects.nonNull(vo.getMerchantCode()), "merchantCode",
                                vo.getMerchantCode())
                        .eq(Objects.nonNull(vo.getCountryCode()), "countryCode",
                                vo.getCountryCode())
                        .eq(Objects.nonNull(vo.getCurrency()), "settlementCurrency",
                                vo.getCurrency())
                        .ne("status", ApprovalBizBatchWithdrawalStatusEnum.CANCELED)
                        .in(ObjectUtils.isNotEmpty(vo.getStatusList()), "status",
                                vo.getStatusList())
                        .ge(Objects.nonNull(vo.getStartDate()), "createdTime", (vo.getStartDate()))
                        .le(Objects.nonNull(vo.getEndDate()), "createdTime", (vo.getEndDate()))
                        .build();

        final Page<ApprovalBizBatchWithdrawal> page =
                approvalBizBatchWithdrawalRepository.findAll(
                        spec, PageRequest.of(vo.getPageNo() - 1, vo.getPageSize(),
                                Sort.by(Sort.Order.desc("createdTime"))),
                        EntityGraphUtils.fromAttributePaths("approval"));

        final List<PageApprovalBizBatchWithdrawalDto> dtoList =
                modelMapper.convertApprovalBatchWithdrawalList(page.getContent());

        final Map<Long, List<ApprovalNodeSimpleDto>> nodeMap = approvalNodeService.list(
                        dtoList.stream().map(PageApprovalBizBatchWithdrawalDto::getApprovalId)
                                .collect(Collectors.toList())).stream()
                .collect(Collectors.groupingBy(ApprovalNodeSimpleDto::getApprovalId));

        dtoList.forEach(v -> v.setNodes(nodeMap.get(v.getApprovalId())));

        return new PageVo<>(vo.getPageNo(), vo.getPageSize(), page.getTotalElements(), dtoList);
    }

    @Override
    public void retryUnfrozenAmount(final MultipartFile file, final Long batchId) {
        try {
            InputStream inputStream = file.getInputStream();
            final List<BatchWithdrawalExcelDataBo> boList = EasyExcel.read(inputStream)
                    .head(BatchWithdrawalExcelDataBo.class)
                    .sheet()
                    .doReadSync();

            final ApprovalBizBatchWithdrawal batchWithdrawal = findByBatchId(batchId);

            final Map<String, DealershipWithdrawalInfoDto> dtoMap =
                    statementFeign.listWithdrawalApply(ListBatchWithdrawalApplyVo.builder()
                                    .batchId(batchWithdrawal.getBatchId()).build()).getData()
                            .stream().collect(
                                    Collectors.toMap(DealershipWithdrawalInfoDto::getSubMerchantId,
                                            obj -> obj));

            this.validExcelBatchWithdrawalData(boList, dtoMap);

            final BatchWithdrawalApprovedHandleVo vo =
                    this.parseExcelData(batchWithdrawal, boList, dtoMap);

            CheckResponseUtil.checkResponse(statementFeign.batchWithdrawalApprovedHandle(vo));

        } catch (Exception e) {
            log.error("retry unfrozen amount failed, msg={}", e.getMessage(), e);
            throw TransactionExceptionCode.RETRY_UNFROZEN_AMOUNT_ERROR.exception(e.getMessage());
        }
    }
}
