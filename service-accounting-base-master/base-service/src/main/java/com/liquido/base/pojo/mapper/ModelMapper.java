package com.liquido.base.pojo.mapper;

import java.util.List;

import com.liquido.base.pojo.dto.AccountFeeConfigurationDto;
import com.liquido.base.pojo.dto.ApmCostConfigDto;
import com.liquido.base.pojo.dto.BizFeeConfigurationDto;
import com.liquido.base.pojo.dto.CardCostConfigDto;
import com.liquido.base.pojo.dto.CostApmConfigDto;
import com.liquido.base.pojo.dto.CostCardConfigDto;
import com.liquido.base.pojo.dto.CostConfigDto;
import com.liquido.base.pojo.dto.CountryProductDto;
import com.liquido.base.pojo.dto.ExtraIncomeConfigurationDto;
import com.liquido.base.pojo.dto.MerchantDto;
import com.liquido.base.pojo.dto.MonthlyFeeConfigurationDto;
import com.liquido.base.pojo.dto.PaymentConfigDto;
import com.liquido.base.pojo.dto.SubMerchantDto;
import com.liquido.base.pojo.entity.AccountFeeConfiguration;
import com.liquido.base.pojo.entity.ApmCostConfiguration;
import com.liquido.base.pojo.entity.BizFeeConfiguration;
import com.liquido.base.pojo.entity.CardCostConfiguration;
import com.liquido.base.pojo.entity.CostConfiguration;
import com.liquido.base.pojo.entity.CountryProduct;
import com.liquido.base.pojo.entity.ExtraIncomeConfiguration;
import com.liquido.base.pojo.entity.Merchant;
import com.liquido.base.pojo.entity.MonthlyFeeConfiguration;
import com.liquido.base.pojo.entity.PaymentConfig;
import com.liquido.base.pojo.entity.SubMerchantInfo;
import com.liquido.base.pojo.vo.EditMerchantVo;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ModelMapper {
    ModelMapper INSTANCE = Mappers.getMapper(ModelMapper.class);

    MerchantDto convert(final Merchant entity);

    List<MerchantDto> convertMerchantList(final List<Merchant> entity);

    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateMerchant(final EditMerchantVo vo, @MappingTarget final Merchant entity);

    CountryProductDto convert(final CountryProduct entity);

    PaymentConfigDto convert(final PaymentConfig entity);

    BizFeeConfigurationDto convert(final BizFeeConfiguration entity);

    List<BizFeeConfigurationDto> convert(final List<BizFeeConfiguration> entityList);

    CostConfigDto convert(final CostConfiguration entity);

    List<CostConfigDto> convertCostConfigDto(final List<CostConfiguration> entityList);

    AccountFeeConfigurationDto convert(final AccountFeeConfiguration entity);

    MonthlyFeeConfigurationDto convert(final MonthlyFeeConfiguration entity);

    List<CardCostConfigDto> convertCardCostConfig(final List<CardCostConfiguration> entityList);

    List<ApmCostConfigDto> convertApmCostConfig(final List<ApmCostConfiguration> entityList);

    List<ExtraIncomeConfigurationDto> convertExtraFeeConfig(
            final List<ExtraIncomeConfiguration> list);

    SubMerchantDto convert(final SubMerchantInfo subMerchantInfo);

    List<SubMerchantDto> convertSubMerchantInfo(final List<SubMerchantInfo> infos);

    CostApmConfigDto convertCostApmConfig(final ApmCostConfigDto costApmConfig);

    CostCardConfigDto convertCostCardConfig(final CardCostConfigDto costCardConfig);
}
