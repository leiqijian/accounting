package com.liquido.statement.pojo.dto;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryAccountUploadTradeDataDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<ListAccountBalanceDto> accountBalanceList;

    private List<DailyExchangeRateDto> dailyExchangeRateList;

}
