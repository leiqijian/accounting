package com.liquido.statement.pojo.dto;

import java.io.Serializable;

import com.liquido.core.mvc.vo.PageVo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SummarySubAccountDailyTransactionDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private SummarySubAccountDto subAccount;

    private PageVo<SubAccountDailyBillDto> subAccountDailyBill;
}
