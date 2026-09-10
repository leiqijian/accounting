package com.liquido.statement.pojo.vo;

import java.io.Serializable;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QueryAccountDailyBillStatisticsVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private LocalDate startDate;

    private LocalDate endDate;

}
