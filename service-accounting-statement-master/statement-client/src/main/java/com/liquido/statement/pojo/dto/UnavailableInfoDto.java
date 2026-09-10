package com.liquido.statement.pojo.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import javax.persistence.Convert;

import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.enums.ProductCodeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnavailableInfoDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<AccountingCalendarDto> accountingCalendarList;

    private AccountingCalendarTotalInfo unavailableTotalInfo;

    private List<AccountingCalendarDetailsInfo> unavailableDetailsInfo;


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AccountingCalendarTotalInfo implements Serializable {

        private static final long serialVersionUID = 1L;

        private BigDecimal settleAmount;

        private BigDecimal accountAmount;

        private BigDecimal holdAmount;

        private BigDecimal feeAmount;

        @Convert(converter = CurrencyEnum.Convert.class)
        private CurrencyEnum currency;
    }


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AccountingCalendarDetailsInfo implements Serializable {

        private static final long serialVersionUID = 1L;

        @Convert(converter = ProductCodeEnum.Convert.class)
        private ProductCodeEnum productCode;

        private BigDecimal settleAmount;

        private BigDecimal accountAmount;

        private BigDecimal holdAmount;

        private BigDecimal feeAmount;

        @Convert(converter = CurrencyEnum.Convert.class)
        private CurrencyEnum currency;
    }
}
