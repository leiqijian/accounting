package com.liquido.transaction.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.AbstractMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import com.liquido.base.pojo.dto.MerchantDto;
import com.liquido.core.common.exception.CommonExceptionCode;
import com.liquido.core.common.security.Md5Util;
import com.liquido.core.common.snowflake.SnowflakeIdUtil;
import com.liquido.core.common.utils.DataUtil;
import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.transaction.aws.s3.AwsFileS3Service;
import com.liquido.transaction.exception.TransactionExceptionCode;
import com.liquido.transaction.feign.BaseService;
import com.liquido.transaction.pojo.dto.AppendixDto;
import com.liquido.transaction.pojo.dto.AppendixUpdateMerchantIdDto;
import com.liquido.transaction.pojo.entity.Appendix;
import com.liquido.transaction.pojo.entity.QApproval;
import com.liquido.transaction.pojo.entity.QApprovalBizExchange;
import com.liquido.transaction.pojo.entity.QApprovalBizPaymentPayout;
import com.liquido.transaction.pojo.entity.QApprovalBizRefund;
import com.liquido.transaction.pojo.entity.QApprovalBizSubMerchant;
import com.liquido.transaction.pojo.entity.QApprovalBizTopup;
import com.liquido.transaction.pojo.entity.QApprovalBizTransferOut;
import com.liquido.transaction.pojo.entity.QApprovalNode;
import com.liquido.transaction.pojo.entity.QChargeBackOrder;
import com.liquido.transaction.pojo.entity.QChargeBackOrderReply;
import com.liquido.transaction.pojo.entity.QRefundOrder;
import com.liquido.transaction.pojo.entity.QRefundOrderReply;
import com.liquido.transaction.pojo.mapper.ModelMapper;
import com.liquido.transaction.pojo.vo.DownloadAppendixVo;
import com.liquido.transaction.pojo.vo.QueryAppendixVo;
import com.liquido.transaction.repository.AppendixRepository;
import com.liquido.transaction.service.AppendixService;

import com.github.wenhao.jpa.Specifications;
import com.google.common.collect.Lists;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackOn = Exception.class)
public class AppendixServiceImpl implements AppendixService {

    private final ModelMapper modelMapper;
    private final AppendixRepository appendixRepository;
    private final AwsFileS3Service awsFileS3Service;
    private final JPAQueryFactory jpaQueryFactory;
    private final BaseService baseService;


    @Override
    public Appendix uploadAppendixAndSave(final MultipartFile file, final String storePath,
                                          final Long merchantId) {
        return uploadAppendixAndSave(file, storePath, "", "", "", merchantId);
    }

    @Override
    public void downloadFile(final Long appendixId, final String filePath) {
        final Specification<Appendix> spec = Specifications.<Appendix>and()
                .eq("id", appendixId).build();
        final Appendix appendix = appendixRepository.findOne(spec)
                .orElseThrow(CommonExceptionCode.DATA_NOT_FOUND::exception);

        try (OutputStream outputStream = new FileOutputStream(filePath)) {
            awsFileS3Service.downloadFile(
                    appendix.getS3Url(), appendix.getFileName(), outputStream);
        } catch (IOException ioException) {
            log.error("download file failed, appendixId: {}", appendix.getId());
            throw TransactionExceptionCode.DOWNLOAD_FILE_FAIL.exception(
                    "appendixId: " + appendix.getId());
        }
    }

    public Appendix uploadAppendixAndSave(final MultipartFile file, final String storePath,
                                          String fileName, String referenceCode,
                                          String s3FileName,
                                          final Long merchantId) {
        try {

            if (StringUtils.isBlank(referenceCode)) {
                referenceCode = String.valueOf(SnowflakeIdUtil.generate());
            }

            if (StringUtils.isBlank(fileName)) {
                fileName = Optional.of(file.getName()).orElse("");
            }

            if (StringUtils.isBlank(s3FileName)) {
                s3FileName = String.join("-", DataUtil.getUuid(), fileName);
            }

            final String fileMd5 = Md5Util.getMultiPartFile(file);

            final String s3Key = awsFileS3Service.uploadFileByPart(file, storePath, s3FileName);

            Appendix appendix = Appendix.builder()
                    .merchantId(merchantId)
                    .s3Url(s3Key).s3FileName(s3FileName)
                    .referenceCode(referenceCode)
                    .fileName(fileName)
                    .fileMd5(fileMd5)
                    .createdTime(LocalDateTimeUtil.nowUtc())
                    .updatedTime(LocalDateTimeUtil.nowUtc()).build();

            return appendixRepository.save(appendix);

        } catch (Exception e) {
            log.error("upload file to s3 failed: {}", e.getMessage(), e);
            throw CommonExceptionCode.FILE_UPLOAD_FAIL.exception();
        }

    }

    @Override
    public Appendix uploadAppendixAndSave(final File file, final String storePath,
                                          final Long merchantId) {
        return uploadAppendixAndSave(file, storePath, null, null, null, merchantId);
    }

    public Appendix uploadAppendixAndSave(final File file, final String storePath,
                                          String fileName, String referenceCode,
                                          String s3FileName,
                                          final Long merchantId) {
        try {

            if (StringUtils.isBlank(referenceCode)) {
                referenceCode = String.valueOf(SnowflakeIdUtil.generate());
            }

            if (StringUtils.isBlank(fileName)) {
                fileName = Optional.of(file.getName()).orElse("");
            }

            if (StringUtils.isBlank(s3FileName)) {
                s3FileName = String.join("-", DataUtil.getUuid(), fileName);
            }

            final String fileMd5 = Md5Util.getFileMd5(file);

            final String s3Key = awsFileS3Service.uploadFileByPart(file, storePath, s3FileName);

            Appendix appendix = Appendix.builder()
                    .merchantId(merchantId)
                    .s3Url(s3Key).s3FileName(s3FileName)
                    .referenceCode(referenceCode)
                    .fileName(fileName)
                    .fileMd5(fileMd5)
                    .createdTime(LocalDateTimeUtil.nowUtc())
                    .updatedTime(LocalDateTimeUtil.nowUtc()).build();

            return appendixRepository.save(appendix);

        } catch (Exception e) {
            log.error("upload file to s3 failed: {}", e.getMessage(), e);
            throw CommonExceptionCode.FILE_UPLOAD_FAIL.exception();
        }
    }


    public Appendix uploadAppendixAndSave(final File file, final String storePath,
                                          final String fileName, final String referenceCode,
                                          final String s3FileName,
                                          final String merchantCode) {

        Long merchantId = 0L;
        if (StringUtils.isNotBlank(merchantCode)) {
            final MerchantDto merchantByCode = baseService.getMerchantByCode(merchantCode);
            merchantId = merchantByCode.getId();
        }
        return uploadAppendixAndSave(file, storePath, fileName, referenceCode, s3FileName,
                merchantId);
    }

    @Override
    public List<AppendixDto> findAllByIds(final List<Long> appendixIds) {
        return ObjectUtils.isEmpty(appendixIds) ? List.of() : modelMapper.convertAppendixList(
                appendixRepository.findAllById(appendixIds));
    }

    @Override
    public void downloadAppendix(final DownloadAppendixVo vo, final HttpServletResponse response) {
        final Specification<Appendix> spec = Specifications.<Appendix>and()
                .eq("id", vo.getId())
                .eq(Objects.nonNull(vo.getMerchantId()), "merchantId", vo.getMerchantId())
                .build();
        final Appendix appendix = appendixRepository.findOne(spec)
                .orElseThrow(CommonExceptionCode.DATA_NOT_FOUND::exception);
        awsFileS3Service.downloadFile(appendix.getS3Url(), appendix.getFileName(), response);
    }

    @Override
    public List<AppendixDto> generatorAppendixDownloadUrl(final QueryAppendixVo vo) {
        if (Objects.isNull(vo) || ObjectUtils.isEmpty(vo.getIds())) {
            return List.of();
        }
        final Specification<Appendix> spec = Specifications.<Appendix>and()
                .in("id", vo.getIds().toArray())
                .eq(Objects.nonNull(vo.getMerchantId()), "merchantId", vo.getMerchantId())
                .build();
        return appendixRepository.findAll(spec).stream().map(v -> {
            final AppendixDto dto = modelMapper.convertAppendix(v);
            dto.setDownloadUrl(awsFileS3Service.generateFileUrl(v.getS3Url(), 360));
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public void appendixMerchantIdAdd() {

        List<AppendixUpdateMerchantIdDto> resultList = Lists.newArrayList();

        //exchange
        final List<AppendixUpdateMerchantIdDto> exchangeData = queryExchangeData();
        //exchange approval node
        final List<AppendixUpdateMerchantIdDto> exchangeApprovalNodeData =
                queryexchangeApprovalNodeData();

        //payment payout
        final List<AppendixUpdateMerchantIdDto> paymentPayoutData = queryPaymentPayoutData();
        //payment payout approval node
        final List<AppendixUpdateMerchantIdDto> paymentPayoutApprovalNodeData =
                queryPaymentPayoutApprovalNodeData();

        //refund
        final List<AppendixUpdateMerchantIdDto> refundPayoutData = queryRefundData();
        //refund approval node
        final List<AppendixUpdateMerchantIdDto> refundApprovalNodeData =
                queryRefundApprovalNodeData();

        //sub_merchant
        final List<AppendixUpdateMerchantIdDto> subMerchantPayoutData = querySubMerchantData();
        //sub_merchant approval node
        final List<AppendixUpdateMerchantIdDto> subMerchantApprovalNodeData =
                querySubMerchantApprovalNodeData();

        //topup
        final List<AppendixUpdateMerchantIdDto> topupData = queryTopupData();
        //topup approval node
        final List<AppendixUpdateMerchantIdDto> topupApprovalNodeData =
                queryTopupApprovalNodeData();

        //transfer out
        final List<AppendixUpdateMerchantIdDto> transferOutData = queryTransferOutData();
        //transfer out approval node
        final List<AppendixUpdateMerchantIdDto> transferOutApprovalNodeData =
                querytransferOutApprovalNodeData();

        //charge back order
        final List<AppendixUpdateMerchantIdDto> chargeBackOrderDataData =
                queryChargeBackOrderData();

        //charge back order reply
        final List<AppendixUpdateMerchantIdDto> chargeBackOrderReplyDataData =
                queryChargeBackOrderReplyData();

        //refund order
        final List<AppendixUpdateMerchantIdDto> refundOrderData = queryRefundOrderData();

        //refund order reply
        final List<AppendixUpdateMerchantIdDto> refundOrderReplyData = queryRefundOrderReplyData();

        resultList.addAll(exchangeData);
        resultList.addAll(exchangeApprovalNodeData);
        resultList.addAll(paymentPayoutData);
        resultList.addAll(paymentPayoutApprovalNodeData);
        resultList.addAll(refundPayoutData);
        resultList.addAll(refundApprovalNodeData);
        resultList.addAll(subMerchantPayoutData);
        resultList.addAll(subMerchantApprovalNodeData);
        resultList.addAll(topupData);
        resultList.addAll(topupApprovalNodeData);
        resultList.addAll(transferOutData);
        resultList.addAll(transferOutApprovalNodeData);
        resultList.addAll(chargeBackOrderDataData);
        resultList.addAll(chargeBackOrderReplyDataData);
        resultList.addAll(refundOrderData);
        resultList.addAll(refundOrderReplyData);

        resultList.stream()
                .flatMap(dto -> dto.getAppendixIds().stream()
                        .map(appendixId -> new AbstractMap.SimpleEntry<>(dto.getMerchantId(),
                                appendixId)))
                .forEach(entry -> appendixRepository.updateMerchantIdById(entry.getKey(),
                        entry.getValue()));
    }

    private List<AppendixUpdateMerchantIdDto> queryRefundOrderReplyData() {
        final QRefundOrder refundOrder = QRefundOrder.refundOrder;
        final QRefundOrderReply refundOrderReply = QRefundOrderReply.refundOrderReply;
        final QBean<AppendixUpdateMerchantIdDto> bean = Projections.fields(
                AppendixUpdateMerchantIdDto.class,
                refundOrder.merchantId,
                refundOrderReply.appendixIds
        );

        final List<AppendixUpdateMerchantIdDto> resultList = jpaQueryFactory.select(bean)
                .from(refundOrder)
                .leftJoin(refundOrderReply)
                .on(refundOrder.id.eq(refundOrderReply.refundOrderId)).fetch();

        return summaryResultList(resultList);
    }

    private List<AppendixUpdateMerchantIdDto> queryRefundOrderData() {
        final QRefundOrder refundOrder = QRefundOrder.refundOrder;
        final QBean<AppendixUpdateMerchantIdDto> bean = Projections.fields(
                AppendixUpdateMerchantIdDto.class,
                refundOrder.merchantId,
                refundOrder.appendixIds
        );

        final List<AppendixUpdateMerchantIdDto> resultList = jpaQueryFactory.select(bean)
                .from(refundOrder).fetch();

        return summaryResultList(resultList);
    }

    private List<AppendixUpdateMerchantIdDto> queryChargeBackOrderReplyData() {
        final QChargeBackOrder chargeBackOrder = QChargeBackOrder.chargeBackOrder;
        final QChargeBackOrderReply chargeBackOrderReply =
                QChargeBackOrderReply.chargeBackOrderReply;
        final QBean<AppendixUpdateMerchantIdDto> bean = Projections.fields(
                AppendixUpdateMerchantIdDto.class,
                chargeBackOrder.merchantId,
                chargeBackOrderReply.appendixIds
        );

        final List<AppendixUpdateMerchantIdDto> resultList = jpaQueryFactory.select(bean)
                .from(chargeBackOrder)
                .leftJoin(chargeBackOrderReply)
                .on(chargeBackOrder.id.eq(chargeBackOrderReply.chargeBackOrderId)).fetch();

        return summaryResultList(resultList);
    }

    private List<AppendixUpdateMerchantIdDto> queryChargeBackOrderData() {
        final QChargeBackOrder chargeBackOrder = QChargeBackOrder.chargeBackOrder;
        final QBean<AppendixUpdateMerchantIdDto> bean = Projections.fields(
                AppendixUpdateMerchantIdDto.class,
                chargeBackOrder.merchantId,
                chargeBackOrder.appendixIds
        );

        final List<AppendixUpdateMerchantIdDto> resultList = jpaQueryFactory.select(bean)
                .from(chargeBackOrder).fetch();

        return summaryResultList(resultList);
    }

    private List<AppendixUpdateMerchantIdDto> querySubMerchantApprovalNodeData() {
        final QApprovalBizSubMerchant approvalBizSubMerchant =
                QApprovalBizSubMerchant.approvalBizSubMerchant;
        final QApprovalNode approvalNode = QApprovalNode.approvalNode;

        final QBean<AppendixUpdateMerchantIdDto> bean = Projections.fields(
                AppendixUpdateMerchantIdDto.class,
                approvalBizSubMerchant.merchantId,
                approvalNode.appendixIds
        );

        final List<AppendixUpdateMerchantIdDto> resultList = jpaQueryFactory.select(bean)
                .from(approvalBizSubMerchant)
                .leftJoin(approvalNode)
                .on(approvalBizSubMerchant.approval.id.eq(approvalNode.approvalId))
                .fetch();

        return summaryResultList(resultList);
    }

    private List<AppendixUpdateMerchantIdDto> querySubMerchantData() {
        final QApprovalBizSubMerchant approvalBizSubMerchant =
                QApprovalBizSubMerchant.approvalBizSubMerchant;
        final QApproval approval = QApproval.approval;

        final QBean<AppendixUpdateMerchantIdDto> bean = Projections.fields(
                AppendixUpdateMerchantIdDto.class,
                approvalBizSubMerchant.merchantId,
                approval.appendixIds
        );

        final List<AppendixUpdateMerchantIdDto> resultList = jpaQueryFactory.select(bean)
                .from(approvalBizSubMerchant)
                .leftJoin(approval)
                .on(approvalBizSubMerchant.approval.id.eq(approval.id))
                .fetch();
        return summaryResultList(resultList);
    }

    private List<AppendixUpdateMerchantIdDto> queryRefundApprovalNodeData() {
        final QApprovalBizRefund approvalBizRefund = QApprovalBizRefund.approvalBizRefund;
        final QApprovalNode approvalNode = QApprovalNode.approvalNode;

        final QBean<AppendixUpdateMerchantIdDto> bean = Projections.fields(
                AppendixUpdateMerchantIdDto.class,
                approvalBizRefund.merchantId,
                approvalNode.appendixIds
        );

        final List<AppendixUpdateMerchantIdDto> resultList = jpaQueryFactory.select(bean)
                .from(approvalBizRefund)
                .leftJoin(approvalNode)
                .on(approvalBizRefund.approval.id.eq(approvalNode.approvalId))
                .fetch();

        return summaryResultList(resultList);
    }

    private List<AppendixUpdateMerchantIdDto> queryRefundData() {
        final QApprovalBizRefund approvalBizRefund = QApprovalBizRefund.approvalBizRefund;
        final QApproval approval = QApproval.approval;

        final QBean<AppendixUpdateMerchantIdDto> bean = Projections.fields(
                AppendixUpdateMerchantIdDto.class,
                approvalBizRefund.merchantId,
                approval.appendixIds
        );

        final List<AppendixUpdateMerchantIdDto> resultList = jpaQueryFactory.select(bean)
                .from(approvalBizRefund)
                .leftJoin(approval)
                .on(approvalBizRefund.approval.id.eq(approval.id))
                .fetch();
        return summaryResultList(resultList);
    }

    private List<AppendixUpdateMerchantIdDto> queryPaymentPayoutApprovalNodeData() {
        final QApprovalBizPaymentPayout approvalBizPaymentPayout =
                QApprovalBizPaymentPayout.approvalBizPaymentPayout;
        final QApprovalNode approvalNode = QApprovalNode.approvalNode;

        final QBean<AppendixUpdateMerchantIdDto> bean = Projections.fields(
                AppendixUpdateMerchantIdDto.class,
                approvalBizPaymentPayout.merchantId,
                approvalNode.appendixIds
        );

        final List<AppendixUpdateMerchantIdDto> resultList = jpaQueryFactory.select(bean)
                .from(approvalBizPaymentPayout)
                .leftJoin(approvalNode)
                .on(approvalBizPaymentPayout.approval.id.eq(approvalNode.approvalId))
                .fetch();
        return summaryResultList(resultList);
    }

    private List<AppendixUpdateMerchantIdDto> queryPaymentPayoutData() {
        final QApprovalBizPaymentPayout approvalBizPaymentPayout =
                QApprovalBizPaymentPayout.approvalBizPaymentPayout;
        final QApproval approval = QApproval.approval;

        final QBean<AppendixUpdateMerchantIdDto> bean = Projections.fields(
                AppendixUpdateMerchantIdDto.class,
                approvalBizPaymentPayout.merchantId,
                approval.appendixIds
        );

        final List<AppendixUpdateMerchantIdDto> resultList = jpaQueryFactory.select(bean)
                .from(approvalBizPaymentPayout)
                .leftJoin(approval)
                .on(approvalBizPaymentPayout.approval.id.eq(approval.id))
                .fetch();
        return summaryResultList(resultList);
    }

    private List<AppendixUpdateMerchantIdDto> queryExchangeData() {
        final QApprovalBizExchange approvalBizExchange = QApprovalBizExchange.approvalBizExchange;
        final QApproval approval = QApproval.approval;

        final QBean<AppendixUpdateMerchantIdDto> bean = Projections.fields(
                AppendixUpdateMerchantIdDto.class,
                approvalBizExchange.merchantId,
                approval.appendixIds
        );

        final List<AppendixUpdateMerchantIdDto> resultList = jpaQueryFactory.select(bean)
                .from(approvalBizExchange)
                .leftJoin(approval)
                .on(approvalBizExchange.approval.id.eq(approval.id))
                .fetch();
        return summaryResultList(resultList);
    }

    private List<AppendixUpdateMerchantIdDto> queryexchangeApprovalNodeData() {
        final QApprovalBizExchange approvalBizExchange = QApprovalBizExchange.approvalBizExchange;
        final QApprovalNode approvalNode = QApprovalNode.approvalNode;

        final QBean<AppendixUpdateMerchantIdDto> bean = Projections.fields(
                AppendixUpdateMerchantIdDto.class,
                approvalBizExchange.merchantId,
                approvalNode.appendixIds
        );

        final List<AppendixUpdateMerchantIdDto> resultList = jpaQueryFactory.select(bean)
                .from(approvalBizExchange)
                .leftJoin(approvalNode)
                .on(approvalBizExchange.approval.id.eq(approvalNode.approvalId))
                .fetch();
        return summaryResultList(resultList);
    }

    private List<AppendixUpdateMerchantIdDto> queryTopupData() {
        final QApprovalBizTopup approvalBizTopup = QApprovalBizTopup.approvalBizTopup;
        final QApproval approval = QApproval.approval;

        final QBean<AppendixUpdateMerchantIdDto> bean = Projections.fields(
                AppendixUpdateMerchantIdDto.class,
                approvalBizTopup.merchantId,
                approval.appendixIds
        );

        final List<AppendixUpdateMerchantIdDto> resultList = jpaQueryFactory.select(bean)
                .from(approvalBizTopup)
                .leftJoin(approval)
                .on(approvalBizTopup.approval.id.eq(approval.id))
                .fetch();
        return summaryResultList(resultList);
    }

    private List<AppendixUpdateMerchantIdDto> queryTopupApprovalNodeData() {
        final QApprovalBizTopup approvalBizTopup = QApprovalBizTopup.approvalBizTopup;
        final QApprovalNode approvalNode = QApprovalNode.approvalNode;

        final QBean<AppendixUpdateMerchantIdDto> bean = Projections.fields(
                AppendixUpdateMerchantIdDto.class,
                approvalBizTopup.merchantId,
                approvalNode.appendixIds
        );

        final List<AppendixUpdateMerchantIdDto> resultList = jpaQueryFactory.select(bean)
                .from(approvalBizTopup)
                .leftJoin(approvalNode)
                .on(approvalBizTopup.approval.id.eq(approvalNode.approvalId))
                .fetch();
        return summaryResultList(resultList);
    }

    private List<AppendixUpdateMerchantIdDto> queryTransferOutData() {
        final QApprovalBizTransferOut approvalBizTransferOut =
                QApprovalBizTransferOut.approvalBizTransferOut;
        final QApproval approval = QApproval.approval;


        final QBean<AppendixUpdateMerchantIdDto> bean = Projections.fields(
                AppendixUpdateMerchantIdDto.class,
                approvalBizTransferOut.merchantId,
                approval.appendixIds
        );

        final List<AppendixUpdateMerchantIdDto> resultList = jpaQueryFactory.select(bean)
                .from(approvalBizTransferOut)
                .leftJoin(approval)
                .on(approvalBizTransferOut.approval.id.eq(approval.id))
                .fetch();
        return summaryResultList(resultList);
    }

    private List<AppendixUpdateMerchantIdDto> querytransferOutApprovalNodeData() {
        final QApprovalBizTransferOut approvalBizTransferOut =
                QApprovalBizTransferOut.approvalBizTransferOut;
        final QApprovalNode approvalNode = QApprovalNode.approvalNode;

        final QBean<AppendixUpdateMerchantIdDto> bean = Projections.fields(
                AppendixUpdateMerchantIdDto.class,
                approvalBizTransferOut.merchantId,
                approvalNode.appendixIds
        );

        final List<AppendixUpdateMerchantIdDto> resultList = jpaQueryFactory.select(bean)
                .from(approvalBizTransferOut)
                .leftJoin(approvalNode)
                .on(approvalBizTransferOut.approval.id.eq(approvalNode.approvalId))
                .fetch();

        return summaryResultList(resultList);
    }

    private List<AppendixUpdateMerchantIdDto> summaryResultList(
            final List<AppendixUpdateMerchantIdDto> resultList) {

        if (CollectionUtils.isEmpty(resultList)) {
            return Lists.newArrayList();
        }

        return resultList.stream()
                .filter(dto -> CollectionUtils.isNotEmpty(dto.getAppendixIds()))
                .filter(dto -> dto.getAppendixIds().size() > 0)
                .collect(Collectors.toList());
    }
}
