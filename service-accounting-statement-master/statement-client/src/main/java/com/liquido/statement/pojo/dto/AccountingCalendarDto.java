package com.liquido.statement.pojo.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
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
public class AccountingCalendarDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private LocalDate accountDate;

    private BigDecimal settleAmount;

    private BigDecimal accountAmount;

    private BigDecimal feeAmount;

    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum currency;

    private List<AccountingCalendarDto.DetailDto> details;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DetailDto implements Serializable {
        private static final long serialVersionUID = 1L;

        @Convert(converter = ProductCodeEnum.Convert.class)
        private ProductCodeEnum productCode;

        private BigDecimal settlementAmount;

        private BigDecimal accountAmount;

        private BigDecimal holdAmount;

        private BigDecimal feeAmount;

        private LocalDate accountDate;
    }
}
