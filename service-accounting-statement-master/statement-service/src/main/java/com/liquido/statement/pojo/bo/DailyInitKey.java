package com.liquido.statement.pojo.bo;

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
public class DailyInitKey implements Serializable {

    private Long accountId;
    /**
     * daily billDate
     */
    private LocalDate transactionDate;
}
