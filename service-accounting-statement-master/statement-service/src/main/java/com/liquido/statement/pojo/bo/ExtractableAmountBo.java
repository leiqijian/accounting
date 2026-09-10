package com.liquido.statement.pojo.bo;

import java.io.Serializable;
import java.math.BigDecimal;

import com.liquido.base.enums.TradingModelEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExtractableAmountBo implements Serializable {
    private static final long serialVersionUID = 1L;

    private TradingModelEnum tradingModel;

    private BigDecimal totalAmount;

}
