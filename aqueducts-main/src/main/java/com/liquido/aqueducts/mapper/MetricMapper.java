package com.liquido.aqueducts.mapper;

import com.liquido.aqueducts.commons.constants.SchemeConsts;
import com.liquido.aqueducts.commons.enums.CountryCode;
import com.liquido.aqueducts.commons.enums.TransactionType;
import com.liquido.aqueducts.config.BaseMapperConfig;
import com.liquido.aqueducts.vo.document.metric.MarketPlaceMetricItem;
import com.liquido.aqueducts.vo.document.metric.PayinMetricItem;
import com.liquido.aqueducts.vo.document.metric.PayoutMetricItem;
import com.liquido.aqueducts.vo.document.metric.SubAccountMetricItem;
import com.liquido.aqueducts.vo.response.TransactionMetricItem;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Mapper(config = BaseMapperConfig.class)
public abstract class MetricMapper {

    private final Map<String, String> countryToCurrencyMap = new HashMap<>();

    @Value("${scheme-config.db.payout}")
    private String dbScheme;

    MetricMapper() {
        countryToCurrencyMap.put("MX", "MXN");
        countryToCurrencyMap.put("CO", "COP");
        countryToCurrencyMap.put("BR", "BRL");
        countryToCurrencyMap.put("CL", "CLP");
        countryToCurrencyMap.put("PE", "PEN");
        countryToCurrencyMap.put("ZA", "ZAR");

        countryToCurrencyMap.put("AR", "ARS");
        countryToCurrencyMap.put("BO", "BOB");
        countryToCurrencyMap.put("CR", "CRC");
        countryToCurrencyMap.put("DO", "DOP");
        countryToCurrencyMap.put("SV", "USD");
        countryToCurrencyMap.put("EC", "USD");
        countryToCurrencyMap.put("GT", "GTQ");
        countryToCurrencyMap.put("HN", "HNL");
        countryToCurrencyMap.put("NI", "NIO");
        countryToCurrencyMap.put("PA", "PAB");
        countryToCurrencyMap.put("PY", "PYG");
        countryToCurrencyMap.put("UY", "UYU");
    }

    @Mapping(target = "countryCode", source = "_id.country")
    @Mapping(target = "countTransaction", source = "count")
    public abstract TransactionMetricItem payoutToMetricItem(final PayoutMetricItem vo);

    @AfterMapping
    void afterMapping(
            @MappingTarget final TransactionMetricItem.TransactionMetricItemBuilder target,
            final PayoutMetricItem source) {
        target.transactionType(TransactionType.PAY_OUT.name());
        // table_name: payout_{merchant}
        target.merchantCode(source.get_id().getMerchant());
        target.currency(countryToCurrencyMap.get(source.get_id().getCountry()));
        BigDecimal sumAmount = new BigDecimal(0);
        for (String amount : source.getAmountList()) {
            sumAmount = sumAmount.add(new BigDecimal(amount));
        }
        if (SchemeConsts.PAY_OUT_TRANSACTIONS_DB_SCHEME.equals(dbScheme)) {
            target.sumAmount(sumAmount.longValue());
        } else {
            target.sumAmount(sumAmount.multiply(new BigDecimal(100)).longValue());
        }
    }

    @Mapping(target = "countryCode", source = "_id.country")
    @Mapping(target = "countTransaction", source = "count")
    @Mapping(target = "sumAmount", source = "sumAmount")
    public abstract TransactionMetricItem payinToMetricItem(final PayinMetricItem vo);

    @AfterMapping
    void afterMapping(
            @MappingTarget final TransactionMetricItem.TransactionMetricItemBuilder target,
            final PayinMetricItem source) {
        target.transactionType(TransactionType.PAY_IN.name());
        target.merchantCode(source.get_id().getMerchant());
        target.currency(countryToCurrencyMap.get(source.get_id().getCountry()));
    }

    @Mapping(target = "countryCode", source = "_id.country")
    @Mapping(target = "countTransaction", source = "count")
    public abstract TransactionMetricItem marketplaceToMetricItem(MarketPlaceMetricItem item);

    @AfterMapping
    void afterMapping(
            @MappingTarget final TransactionMetricItem.TransactionMetricItemBuilder target,
            final MarketPlaceMetricItem source) {
        target.transactionType(TransactionType.MARKET_PLACE_ORDERS.name());
        // table_name: orders_{merchant}
        target.merchantCode(source.get_id().getMerchant().substring(7));
        target.currency(countryToCurrencyMap.get(source.get_id().getCountry()));
        BigDecimal sumAmount = new BigDecimal(0);
        for (String amount : source.getAmountList()) {
            if (StringUtils.hasLength(amount)) {
                sumAmount = sumAmount.add(new BigDecimal(amount));
            }
        }
        final BigDecimal amountUnit = new BigDecimal(100);
        target.sumAmount(sumAmount.multiply(amountUnit).longValue());
    }

    @Mapping(target = "countTransaction", source = "count")
    public abstract TransactionMetricItem subAccountToMetricItem(SubAccountMetricItem item);

    @AfterMapping
    void afterMapping(
            @MappingTarget final TransactionMetricItem.TransactionMetricItemBuilder target,
            final SubAccountMetricItem source) {
        target.transactionType(TransactionType.PAY_IN.name());
        // table_name: sub_account_payback_{merchant}
        target.merchantCode(source.get_id().substring(20));
        target.countryCode(CountryCode.MX.name());
        target.currency("MXN");

        BigDecimal sumAmount = new BigDecimal(0);
        for (String amount : source.getAmountList()) {
            sumAmount = sumAmount.add(new BigDecimal(amount));
        }
        final BigDecimal amountUnit = new BigDecimal(100);
        target.sumAmount(sumAmount.multiply(amountUnit).longValue());
    }
}
