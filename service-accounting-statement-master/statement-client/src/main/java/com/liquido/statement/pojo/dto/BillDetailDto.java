package com.liquido.statement.pojo.dto;

import java.io.Serializable;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("PMD.TooManyFields")
public class BillDetailDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int type;
    private String currency;
    private LocalDate statisticsDate;
    private int beginBalance;
    private int balance;
    private int depositeAmount;
    private int depositeFee;
    private int exchangeAmount;
    private int exchangeFee;
    private int payoutAmount;
    private int payoutFee;
    private int payoutOrderCount;
    private int paybackAmount;
    private int paybackFee;
    private int paybackOrderCount;
    private int transferAmount;
    private int transferFee;
    private int payoutProcessingFee;
    private int paybackPayerTax1;
    private int paybackPayerTax2;
}
