package com.liquido.base.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.liquido.base.constant.dynamic.DynamicConstant;
import com.liquido.base.enums.CostTypeEnum;
import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.base.enums.FeeGroupEnum;
import com.liquido.base.enums.FeeOnEnum;
import com.liquido.base.enums.FeeTypeCodeEnum;
import com.liquido.base.enums.ProductCodeEnum;
import com.liquido.base.enums.TaxRuleEnum;
import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.base.enums.VendorCodeEnum;
import com.liquido.base.exception.BaseExceptionCode;
import com.liquido.base.pojo.dto.ApmCostConfigDto;
import com.liquido.base.pojo.dto.ApmCostConfigurationDto;
import com.liquido.base.pojo.dto.CardCostConfigDto;
import com.liquido.base.pojo.dto.CardCostConfigurationDto;
import com.liquido.base.pojo.dto.CostApmConfigDto;
import com.liquido.base.pojo.dto.CostApmConfigInitDto;
import com.liquido.base.pojo.dto.CostApmConfigurationDto;
import com.liquido.base.pojo.dto.CostCardConfigDto;
import com.liquido.base.pojo.dto.CostCardConfigInitDto;
import com.liquido.base.pojo.dto.CostCardConfigurationDto;
import com.liquido.base.pojo.dto.CostConfigDto;
import com.liquido.base.pojo.dto.CostConfigInitDto;
import com.liquido.base.pojo.dto.MerchantDto;
import com.liquido.base.pojo.entity.ApmCostConfiguration;
import com.liquido.base.pojo.entity.CardCostConfiguration;
import com.liquido.base.pojo.entity.CostConfiguration;
import com.liquido.base.pojo.entity.QApmCostConfiguration;
import com.liquido.base.pojo.entity.QCardCostConfiguration;
import com.liquido.base.pojo.entity.QCostConfiguration;
import com.liquido.base.pojo.mapper.ModelMapper;
import com.liquido.base.pojo.vo.CostConfigurationVo;
import com.liquido.base.pojo.vo.CreateCostConfigurationVo;
import com.liquido.base.pojo.vo.DeleteCostConfigurationVo;
import com.liquido.base.pojo.vo.QueryApmCostConfigVo;
import com.liquido.base.pojo.vo.QueryCardCostConfigVo;
import com.liquido.base.pojo.vo.QueryCostApmConfigVo;
import com.liquido.base.pojo.vo.QueryCostCardConfigVo;
import com.liquido.base.pojo.vo.QueryCostConfigurationByIdVo;
import com.liquido.base.pojo.vo.QueryCostConfigurationVo;
import com.liquido.base.repository.ApmCostConfigurationRepository;
import com.liquido.base.repository.CardCostConfigurationRepository;
import com.liquido.base.repository.CostConfigurationRepository;
import com.liquido.base.service.CostConfigurationService;
import com.liquido.base.service.CostConfigurationVersionService;
import com.liquido.base.service.MerchantService;
import com.liquido.core.common.snowflake.SnowflakeIdUtil;
import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.core.mvc.vo.PageVo;

import com.github.wenhao.jpa.PredicateBuilder;
import com.github.wenhao.jpa.Specifications;
import com.google.common.collect.Lists;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class CostConfigurationServiceImpl implements CostConfigurationService {

    private final ModelMapper modelMapper;
    private final JPAQueryFactory jpaQueryFactory;
    private final CostConfigurationRepository costConfigurationRepository;
    private final CardCostConfigurationRepository cardCostConfigurationRepository;
    private final ApmCostConfigurationRepository apmCostConfigurationRepository;
    private final CostConfigurationVersionService costConfigurationVersionService;
    private final MerchantService merchantService;

    @Override
    public ApmCostConfigurationDto queryApmCostConfig(
            final QueryApmCostConfigVo vo) {

        List<ApmCostConfiguration> configList = Lists.newArrayList();
        if (Objects.nonNull(vo)
                && Objects.nonNull(vo.getActiveVersion())
                && vo.getActiveVersion() > 0) {
            configList =
                    apmCostConfigurationRepository.findByActiveVersion(vo.getActiveVersion());
        }

        if (CollectionUtils.isEmpty(configList)) {
            final int activeVersion =
                    costConfigurationVersionService.queryActiveConfig(CostTypeEnum.APM);
            configList = apmCostConfigurationRepository.findByActiveVersion(activeVersion);
        }

        return ApmCostConfigurationDto.builder().costConfigList(
                Optional.ofNullable(modelMapper.convertApmCostConfig(configList))
                        .orElse(Collections.emptyList())).build();
    }

    @Override
    public CardCostConfigurationDto queryCardCostConfig() {

        final int activeVersion =
                costConfigurationVersionService.queryActiveConfig(CostTypeEnum.CARD);

        final List<CardCostConfiguration> configList =
                cardCostConfigurationRepository.findByActiveVersion(activeVersion);

        return CardCostConfigurationDto.builder().cardCostConfigList(
                Optional.ofNullable(modelMapper.convertCardCostConfig(configList))
                        .orElse(Collections.emptyList())).build();
    }

    @Override
    public CardCostConfigurationDto queryCardCostConfigByVersion(final QueryCardCostConfigVo vo) {
        List<CardCostConfiguration> configList = Lists.newArrayList();
        if (Objects.nonNull(vo)
                && Objects.nonNull(vo.getActiveVersion())
                && vo.getActiveVersion() > 0) {
            configList =
                    cardCostConfigurationRepository.findByActiveVersion(vo.getActiveVersion());
        }

        if (CollectionUtils.isEmpty(configList)) {
            final int activeVersion =
                    costConfigurationVersionService.queryActiveConfig(CostTypeEnum.CARD);
            configList = cardCostConfigurationRepository.findByActiveVersion(activeVersion);
        }

        return CardCostConfigurationDto.builder().cardCostConfigList(
                Optional.ofNullable(modelMapper.convertCardCostConfig(configList))
                        .orElse(Collections.emptyList())).build();
    }

    @Override
    public PageVo<CostConfigDto> queryCostConfig(final QueryCostConfigurationVo vo) {
        final PredicateBuilder<CostConfiguration> spec = Specifications.and();
        spec.eq(Objects.nonNull(vo.getCountryCode()), "countryCode", vo.getCountryCode());
        spec.eq(Objects.nonNull(vo.getTransactionTypeCode()), "transactionTypeCode",
                vo.getTransactionTypeCode());
        spec.eq(Objects.nonNull(vo.getVendorCode()), "vendorCode", vo.getVendorCode());
        spec.eq(Objects.nonNull(vo.getProductCode()), "productCode", vo.getProductCode());
        spec.eq(Objects.nonNull(vo.getFeeTypeCode()), "feeType", vo.getFeeTypeCode());
        spec.eq(Objects.nonNull(vo.getFeeOnCode()), "feeOn", vo.getFeeOnCode());
        spec.eq("activeMonth", vo.getActiveMonth());
        spec.in(Objects.nonNull(vo.getAccountId()), "accountId",
                ListUtils.emptyIfNull(vo.getAccountId()).toArray());

        final Page<CostConfiguration> costConfigurationPage = costConfigurationRepository.findAll(
                spec.build(), PageRequest.of(vo.getPageNo() - 1, vo.getPageSize()));
        final List<CostConfigDto> resultList =
                modelMapper.convertCostConfigDto(costConfigurationPage.getContent());
        return new PageVo<>(vo.getPageNo(), vo.getPageSize(),
                costConfigurationPage.getTotalElements(), resultList);
    }

    @Override
    public void deleteCostConfig(final DeleteCostConfigurationVo vo) {
        final QCostConfiguration entity = QCostConfiguration.costConfiguration;

        final CostConfiguration costConfiguration =
                jpaQueryFactory.select(entity).from(entity).where(entity.id.eq(vo.getId())
                                .and(entity.accountId.eq(vo.getAccountId())))
                        .fetchFirst();
        if (costConfiguration == null) {
            return;
        }

        //If cost config fee type is TRANSACTION_FEE, need to verify
        if (FeeTypeCodeEnum.TRANSACTION_FEE.equals(costConfiguration.getFeeType())) {
            final BooleanExpression customCondition =
                    entity.accountId.eq(costConfiguration.getAccountId())
                            .and(entity.countryCode.eq(costConfiguration.getCountryCode()))
                            .and(entity.transactionTypeCode.eq(
                                    costConfiguration.getTransactionTypeCode()))
                            .and(entity.vendorCode.eq(costConfiguration.getVendorCode()))
                            .and(entity.productCode.eq(costConfiguration.getProductCode()))
                            .and(entity.activeVersion.eq(costConfiguration.getActiveVersion()))
                            .and(entity.feeOn.eq(FeeOnEnum.FEE));

            final List<CostConfiguration> costConfigurations =
                    jpaQueryFactory.select(entity).from(entity).where(customCondition).fetch();
            if (!CollectionUtils.isEmpty(costConfigurations)) {
                throw BaseExceptionCode.COST_CONFIG_OCCUPY.exception();
            }
        }
        jpaQueryFactory.delete(entity)
                .where(entity.id.eq(vo.getId()))
                .execute();
    }

    @Override
    public void addOrUpdateCostConfig(final CreateCostConfigurationVo vo) {
        final QCostConfiguration entity = QCostConfiguration.costConfiguration;
        final List<CostConfiguration> costConfigurations = new ArrayList<>();

        for (final CostConfigurationVo costConfigurationVo : vo.getConfigList()) {
            final BooleanExpression customCondition = entity.accountId
                    .eq(costConfigurationVo.getAccountId())
                    .and(entity.activeVersion.eq(costConfigurationVo.getActiveVersion()))
                    .and(entity.vendorCode.eq(costConfigurationVo.getVendorCode()))
                    .and(entity.productCode.eq(costConfigurationVo.getProductCode()))
                    .and(entity.feeType.eq(costConfigurationVo.getFeeType()));

            final CostConfiguration costConfigurationDb = jpaQueryFactory.select(entity)
                    .from(entity)
                    .where(customCondition).fetchFirst();

            final CostConfiguration costConfiguration = CostConfiguration.builder()
                    .id(Objects.isNull(costConfigurationDb) ? SnowflakeIdUtil.generate() :
                            costConfigurationDb.getId())
                    .accountId(costConfigurationVo.getAccountId())
                    .countryCode(costConfigurationVo.getCountryCode())
                    .transactionTypeCode(costConfigurationVo.getTransactionTypeCode())
                    .vendorCode(costConfigurationVo.getVendorCode())
                    .productCode(costConfigurationVo.getProductCode())
                    .activeVersion(costConfigurationVo.getActiveVersion())
                    .feeModel(costConfigurationVo.getFeeModel())
                    .taxRule(TaxRuleEnum.EXCLUDING_TAX)
                    .feeName(costConfigurationVo.getFeeName())
                    .feeType(costConfigurationVo.getFeeType())
                    .feeOn(costConfigurationVo.getFeeOn())
                    .volume(costConfigurationVo.getVolume())
                    .currency(costConfigurationVo.getCurrency())
                    .createdTime(LocalDateTimeUtil.nowUtc())
                    .updatedTime(LocalDateTimeUtil.nowUtc())
                    .createdBy(0L)
                    .updatedBy(0L)
                    .delFlag(false)
                    .version(Objects.isNull(costConfigurationDb) ? 1
                            : costConfigurationDb.getVersion() + 1)
                    .remark("")
                    .build();

            costConfigurations.add(costConfiguration);
        }
        costConfigurationRepository.saveAllAndFlush(costConfigurations);
    }

    @Override
    public void updateCostConfig(final CostConfigurationVo vo) {
        final PredicateBuilder<CostConfiguration> spec = Specifications.and();
        spec.eq(Objects.nonNull(vo.getAccountId()), "accountId",
                vo.getAccountId());
        spec.eq("activeMonth", vo.getActiveVersion());
        spec.eq(Objects.nonNull(vo.getVendorCode()), "vendorCode", vo.getVendorCode());
        spec.eq(Objects.nonNull(vo.getProductCode()), "productCode", vo.getProductCode());
        spec.eq(Objects.nonNull(vo.getFeeType()), "feeType", vo.getFeeType());
        spec.eq(Objects.nonNull(vo.getTransactionTypeCode()), "transactionTypeCode",
                vo.getTransactionTypeCode());

        final Optional<CostConfiguration> costConfiguration =
                costConfigurationRepository.findOne(spec.build());

        if (costConfiguration.isEmpty()) {
            throw BaseExceptionCode.COST_CONFIG_NOT_FOUND.exception();
        }

        final QCostConfiguration entity = QCostConfiguration.costConfiguration;

        jpaQueryFactory.update(entity)
                .set(entity.feeModel, vo.getFeeModel())
                .set(entity.feeName, vo.getFeeName())
                .set(entity.feeOn, vo.getFeeOn())
                .set(entity.volume, vo.getVolume())
                .set(entity.currency, vo.getCurrency())
                .set(entity.version, entity.version.add(1))
                .set(entity.updatedTime, LocalDateTimeUtil.nowUtc())
                .where(entity.id.eq(costConfiguration.get().getId()))
                .execute();
    }

    @Override
    public CostConfigDto queryCostConfigById(final QueryCostConfigurationByIdVo vo) {
        final QCostConfiguration entity = QCostConfiguration.costConfiguration;
        final CostConfiguration costConfiguration = jpaQueryFactory.select(entity)
                .from(entity)
                .where(entity.id.eq(vo.getId()))
                .fetchOne();

        if (Objects.isNull(costConfiguration)) {
            throw BaseExceptionCode.COST_CONFIG_NOT_FOUND.exception();
        }

        return modelMapper.convert(costConfiguration);
    }

    @Override
    public CostConfigInitDto queryCostConfigInitList() {

        final List<VendorCodeEnum> vendorCodeEnums = DynamicConstant.values(VendorCodeEnum.class);
        return CostConfigInitDto.builder()
                .vendorCodeList(vendorCodeEnums)
                .productCodeList(Lists.newArrayList(ProductCodeEnum.values()))
                .feeTypeCodeList(Lists.newArrayList(FeeTypeCodeEnum.values()))
                .feeOnList(Lists.newArrayList(FeeOnEnum.values()))
                .build();
    }

    @Override
    public CostApmConfigInitDto initCostApmConfigList() {
        final List<MerchantDto> merchantDtoList = merchantService.queryAllMerchant();

        final Map<String, Long> merchantCodeIdMap = merchantDtoList.stream()
                .collect(Collectors.toMap(MerchantDto::getCode, MerchantDto::getId));

        final List<TransactionTypeCodeEnum> transactionTypeCodeList =
                List.of(TransactionTypeCodeEnum.PAY_IN, TransactionTypeCodeEnum.PAY_OUT);

        final QApmCostConfiguration entity = QApmCostConfiguration.apmCostConfiguration;
        final List<ApmCostConfiguration> apmCostConfigurationList = jpaQueryFactory.select(entity)
                .from(entity)
                .groupBy(entity.vendorCode, entity.countryCode)
                .fetch();

        final Map<CountryCodeEnum, List<VendorCodeEnum>> countryVendorMap =
                apmCostConfigurationList.stream()
                        .filter(config -> config.getVendorCode() != null)
                        .collect(Collectors.groupingBy(
                                ApmCostConfiguration::getCountryCode,
                                Collectors.mapping(
                                        ApmCostConfiguration::getVendorCode,
                                        Collectors.toList()
                                )
                        ));

        final List<Integer> activeVersionList =
                apmCostConfigurationList.stream().map(ApmCostConfiguration::getActiveVersion)
                        .distinct().collect(
                                Collectors.toList());

        return CostApmConfigInitDto.builder()
                .activeVersionList(activeVersionList)
                .merchantCodeIdMap(merchantCodeIdMap)
                .transactionTypeCodeList(transactionTypeCodeList)
                .countryVendorDtoList(countryVendorMap)
                .build();

    }

    @Override
    public CostCardConfigInitDto initCostCardConfigList() {
        final List<MerchantDto> merchantDtoList = merchantService.queryAllMerchant();
        final Map<String, Long> merchantCodeIdMap = merchantDtoList.stream()
                .collect(Collectors.toMap(MerchantDto::getCode, MerchantDto::getId));

        final QCardCostConfiguration entity = QCardCostConfiguration.cardCostConfiguration;
        final List<CardCostConfiguration> cardCostConfigurationList = jpaQueryFactory.select(entity)
                .from(entity)
                .groupBy(entity.vendorCode, entity.countryCode)
                .fetch();

        final List<Integer> activeVersionList =
                cardCostConfigurationList.stream().map(CardCostConfiguration::getActiveVersion)
                        .distinct().collect(
                                Collectors.toList());

        final Map<CountryCodeEnum, List<VendorCodeEnum>> countryVendorMap =
                cardCostConfigurationList.stream()
                        .filter(config -> config.getVendorCode() != null)
                        .collect(Collectors.groupingBy(
                                CardCostConfiguration::getCountryCode,
                                Collectors.mapping(
                                        CardCostConfiguration::getVendorCode,
                                        Collectors.toList()
                                )
                        ));

        return CostCardConfigInitDto.builder()
                .activeVersionList(activeVersionList)
                .merchantCodeIdMap(merchantCodeIdMap)
                .countryVendorDtoList(countryVendorMap)
                .build();
    }

    @Override
    public CostApmConfigurationDto costApmConfigList(final QueryCostApmConfigVo vo) {
        final List<CostApmConfigDto> resultList = Lists.newArrayList();

        final ApmCostConfigurationDto apmCostConfigurationDto = this.queryApmCostConfig(
                QueryApmCostConfigVo.builder().activeVersion(vo.getActiveVersion()).build());

        //load all apm config
        final List<ApmCostConfigDto> configList = apmCostConfigurationDto.getCostConfigList();

        /* Customize(FEE) config */
        final List<ApmCostConfigDto> costFeeList = configList.stream()
                .filter(item -> item.getAccountId().equals(vo.getAccountId())
                        &&
                        (Objects.isNull(vo.getVendor()) || item.getVendorCode() == vo.getVendor())
                        && FeeGroupEnum.TRANSACTION_FEE == item.getFeeGroup())
                .collect(Collectors.toList());

        /* if no customize FEE, then use default apm config */
        if (CollectionUtils.isEmpty(costFeeList)) {
            costFeeList.addAll(configList.stream()
                    .filter(item -> item.getAccountId().equals(0L)
                            && item.getCountryCode() == vo.getCountry()
                            && item.getTransactionTypeCode() == vo.getTransactionType()
                            && (Objects.isNull(vo.getVendor())
                            || item.getVendorCode() == vo.getVendor())
                            && FeeGroupEnum.TRANSACTION_FEE == item.getFeeGroup())
                    .collect(Collectors.toList()));

            final List<CostApmConfigDto> feeList = costFeeList.stream().map(conf -> {
                CostApmConfigDto costApmConfigDto = modelMapper.convertCostApmConfig(conf);
                costApmConfigDto.setDef(true);
                return costApmConfigDto;
            }).collect(Collectors.toList());
            resultList.addAll(feeList);
        } else {
            final List<CostApmConfigDto> feeList = costFeeList.stream().map(conf -> {
                CostApmConfigDto costApmConfigDto = modelMapper.convertCostApmConfig(conf);
                costApmConfigDto.setDef(false);
                return costApmConfigDto;
            }).collect(Collectors.toList());
            resultList.addAll(feeList);
        }

        /* Customize(TAX, FX) config */
        final List<ApmCostConfigDto> costTaxFxList = configList.stream()
                .filter(item -> item.getAccountId().equals(vo.getAccountId())
                        && FeeGroupEnum.TAX_FX_GROUP.contains(item.getFeeGroup()))
                .collect(Collectors.toList());

        /* if no customize(TAX, FX), then use default tax fx config */
        if (CollectionUtils.isEmpty(costTaxFxList)) {
            costTaxFxList.addAll(configList.stream()
                    .filter(item -> item.getAccountId().equals(0L)
                            && FeeGroupEnum.TAX_FX_GROUP.contains(item.getFeeGroup())
                            && item.getCountryCode() == vo.getCountry()
                            && item.getTransactionTypeCode() == vo.getTransactionType()
                            && item.getVendorCode() == vo.getVendor())
                    .collect(Collectors.toList()));

            costTaxFxList.addAll(configList.stream()
                    .filter(item -> item.getAccountId().equals(0L)
                            && FeeGroupEnum.TAX_FX_GROUP.contains(item.getFeeGroup())
                            && item.getCountryCode() == vo.getCountry()
                            && item.getTransactionTypeCode() == vo.getTransactionType()
                            && (Objects.isNull(item.getVendorCode())))
                    .collect(Collectors.toList()));

            final List<CostApmConfigDto> taxFxList = costTaxFxList.stream().map(conf -> {
                CostApmConfigDto costApmConfigDto = modelMapper.convertCostApmConfig(conf);
                costApmConfigDto.setDef(true);
                return costApmConfigDto;
            }).collect(Collectors.toList());
            resultList.addAll(taxFxList);
        } else {
            final List<CostApmConfigDto> taxFxList = costTaxFxList.stream().map(conf -> {
                CostApmConfigDto costApmConfigDto = modelMapper.convertCostApmConfig(conf);
                costApmConfigDto.setDef(false);
                return costApmConfigDto;
            }).collect(Collectors.toList());
            resultList.addAll(taxFxList);
        }

        // deduplication
        final List<CostApmConfigDto> dataList = Lists.newArrayList(resultList.stream()
                .collect(Collectors.toMap(CostApmConfigDto::getId,
                        Function.identity(), (x, y) -> x)).values());

        return CostApmConfigurationDto.builder().costApmConfigDtoList(dataList).build();
    }

    @Override
    public CostCardConfigurationDto costCardConfigList(final QueryCostCardConfigVo vo) {
        final List<CostCardConfigDto> resultList = Lists.newArrayList();

        final CardCostConfigurationDto cardConfigDto = this.queryCardCostConfigByVersion(
                QueryCardCostConfigVo.builder().activeVersion(vo.getActiveVersion()).build());

        //load all card cost config
        final List<CardCostConfigDto> configList = cardConfigDto.getCardCostConfigList();

        /*no Customize(FEE) config */
        final List<CardCostConfigDto> cardDefaultFeeConfig =
                configList.stream().filter(item -> item.getAccountId().equals(0L)
                        && FeeGroupEnum.TRANSACTION_FEE == item.getFeeGroup()
                        && vo.getCountry().equals(item.getCountryCode())
                        && (Objects.isNull(vo.getCardType())
                        || vo.getCardType().equals(item.getCardType()))
                        && (Objects.isNull(vo.getVendor())
                        || vo.getVendor().equals(item.getVendorCode()))
                ).collect(Collectors.toList());

        final List<CostCardConfigDto> defaultFeeList = cardDefaultFeeConfig.stream().map(conf -> {
            CostCardConfigDto cardConfig = modelMapper.convertCostCardConfig(conf);
            cardConfig.setDef(true);
            return cardConfig;
        }).collect(Collectors.toList());

        resultList.addAll(defaultFeeList);

        /*Customize(FEE) config */
        final List<CardCostConfigDto> cardCustomizeFeeList = configList.stream()
                .filter(item -> item.getAccountId().equals(vo.getAccountId())
                        && FeeGroupEnum.TRANSACTION_FEE == item.getFeeGroup())
                .collect(Collectors.toList());

        final List<CostCardConfigDto> customizeList = cardCustomizeFeeList.stream().map(conf -> {
            CostCardConfigDto cardConfig = modelMapper.convertCostCardConfig(conf);
            cardConfig.setDef(false);
            return cardConfig;
        }).collect(Collectors.toList());

        resultList.addAll(customizeList);

        /*Customize(Tax, Fx) config */
        final List<CardCostConfigDto> cardTaxFxConfig =
                configList.stream()
                        .filter(item -> item.getAccountId().equals(vo.getAccountId())
                                && FeeGroupEnum.TAX_FX_GROUP.contains(item.getFeeGroup())
                        ).collect(Collectors.toList());

        final List<CostCardConfigDto> taxFxList = cardTaxFxConfig.stream().map(conf -> {
            CostCardConfigDto cardConfig = modelMapper.convertCostCardConfig(conf);
            cardConfig.setDef(false);
            return cardConfig;
        }).collect(Collectors.toList());

        resultList.addAll(taxFxList);

        if (CollectionUtils.isEmpty(cardTaxFxConfig)) {
            final List<CardCostConfigDto> defaultTaxFxList = configList.stream()
                    .filter(item -> FeeGroupEnum.TAX_FX_GROUP.contains(item.getFeeGroup())
                            && item.getAccountId().equals(0L)
                            && item.getCountryCode().equals(vo.getCountry())
                    ).collect(Collectors.toList());

            final List<CostCardConfigDto> list = defaultTaxFxList.stream().map(conf -> {
                CostCardConfigDto cardConfig = modelMapper.convertCostCardConfig(conf);
                cardConfig.setDef(true);
                return cardConfig;
            }).collect(Collectors.toList());

            resultList.addAll(list);
        }

        return CostCardConfigurationDto.builder().costCardConfigDtoList(resultList).build();
    }
}
