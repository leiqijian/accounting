package com.liquido.base.service.impl;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.liquido.base.common.Constant;
import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.base.enums.OperationMethodEnum;
import com.liquido.base.enums.PaymentChannelEnum;
import com.liquido.base.enums.PaymentConfigStatusEnum;
import com.liquido.base.exception.BaseExceptionCode;
import com.liquido.base.pojo.dto.BatchPaymentConfigCreateDto;
import com.liquido.base.pojo.dto.PaymentConfigDto;
import com.liquido.base.pojo.entity.PaymentConfig;
import com.liquido.base.pojo.entity.QPaymentConfig;
import com.liquido.base.pojo.mapper.ModelMapper;
import com.liquido.base.pojo.vo.BatchCreatePaymentLinkPaymentConfigVo;
import com.liquido.base.pojo.vo.BatchCreatePayoutPaymentConfigVo;
import com.liquido.base.pojo.vo.DefaultPaymentConfigVo;
import com.liquido.base.pojo.vo.QueryPaymentConfigVo;
import com.liquido.base.repository.PaymentConfigRepository;
import com.liquido.base.service.PaymentConfigService;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentConfigServiceImpl implements PaymentConfigService {

    private final ModelMapper modelMapper;
    private final JPAQueryFactory jpaQueryFactory;
    private final PaymentConfigRepository paymentConfigRepository;

    @Override
    public BatchPaymentConfigCreateDto batchCreatePayoutPaymentConfig(
            final BatchCreatePayoutPaymentConfigVo vo) {
        final List<PaymentConfig> paymentConfigs =
                doCreateDefaultPaymentConfigs(vo.getConfigItems());

        final List<PaymentConfigDto> paymentConfigDtoList = paymentConfigs.stream()
                .map(modelMapper::convert)
                .collect(Collectors.toList());

        return BatchPaymentConfigCreateDto.builder()
                .paymentConfigDtoList(paymentConfigDtoList)
                .build();
    }

    @Override
    public BatchPaymentConfigCreateDto batchCreatePaymentLinkPaymentConfig(
            final BatchCreatePaymentLinkPaymentConfigVo vo) {
        final List<PaymentConfig> paymentConfigs =
                doCreateDefaultPaymentConfigs(vo.getConfigItems());

        final List<PaymentConfigDto> paymentConfigDtoList = paymentConfigs.stream()
                .map(modelMapper::convert)
                .collect(Collectors.toList());

        return BatchPaymentConfigCreateDto.builder()
                .paymentConfigDtoList(paymentConfigDtoList)
                .build();
    }

    private List<PaymentConfig> doCreateDefaultPaymentConfigs(
            final List<? extends DefaultPaymentConfigVo> configVoList) {
        final List<Long> accountIdList = configVoList.stream()
                .map(DefaultPaymentConfigVo::getAccountId)
                .collect(Collectors.toList());

        log.info("create default payment configs for accounts: {}", accountIdList);

        if (existsByAccountIds(accountIdList)) {
            log.error("account payment config already exists, current accountIdList: {}",
                    accountIdList);
            throw BaseExceptionCode.PAYMENT_CONFIG_ALREADY_EXISTS.exception();
        }

        final List<PaymentConfig> defaultPaymentConfigs = configVoList.stream()
                .map(this::buildDefaultPaymentConfig)
                .collect(Collectors.toList());

        return paymentConfigRepository.saveAllAndFlush(defaultPaymentConfigs);
    }

    private PaymentConfig buildDefaultPaymentConfig(final DefaultPaymentConfigVo configVo) {
        final CountryCodeEnum countryCode = configVo.getCountryCode();
        return PaymentConfig.builder()
                .accountId(configVo.getAccountId())
                .paymentChannel(PaymentChannelEnum.getDefaultChannel(countryCode))
                .priority(Constant.PaymentConfig.DEFAULT_PRIORITY)
                .operationMethod(OperationMethodEnum.MANUAL)
                .maxAmount(Objects.requireNonNullElse(
                        configVo.getMaxAmount(),
                        Constant.PaymentConfig.DEFAULT_MAX_AMOUNT)
                )
                .apiKey(configVo.getTransactionApiKey())
                .jsonParams(configVo.getJsonParam())
                .delayExecution(Constant.PaymentConfig.DEFAULT_DELAY_EXECUTION)
                .status(PaymentConfigStatusEnum.ENABLE)
                .version(1L)
                .mockSwitch(false)
                .remark(configVo.getRemark())
                .build();
    }

    @Override
    public boolean existsByAccountIds(final Collection<Long> accountIds) {
        return paymentConfigRepository.existsByAccountIds(accountIds);
    }

    @Override
    public PaymentConfigDto queryPaymentConfig(final QueryPaymentConfigVo vo) {
        final QPaymentConfig entity = QPaymentConfig.paymentConfig;
        BooleanExpression condition = entity.accountId.eq(vo.getAccountId())
                .and(entity.status.eq(PaymentConfigStatusEnum.ENABLE))
                .and(entity.delFlag.eq(Boolean.FALSE));

        if (Objects.nonNull(vo.getOperationMethod())) {
            condition = condition.and(entity.operationMethod.eq(vo.getOperationMethod()));
        }

        if (Objects.nonNull(vo.getPaymentChannel())) {
            condition = condition.and(entity.paymentChannel.eq(vo.getPaymentChannel()));
        }

        final PaymentConfig paymentConfig = jpaQueryFactory.select(entity)
                .from(entity)
                .where(condition)
                .orderBy(entity.priority.asc())
                .fetchOne();

        if (Objects.isNull(paymentConfig)) {
            return null;
        }

        return modelMapper.convert(paymentConfig);
    }

    @Override
    public PaymentConfigDto queryPaymentConfig(final Long configId) {

        final QPaymentConfig entity = QPaymentConfig.paymentConfig;
        final PaymentConfig paymentConfig = jpaQueryFactory.select(entity)
                .from(entity)
                .where(entity.id.eq(configId))
                .fetchOne();

        if (Objects.isNull(paymentConfig)) {
            return null;
        }

        return modelMapper.convert(paymentConfig);
    }
}
