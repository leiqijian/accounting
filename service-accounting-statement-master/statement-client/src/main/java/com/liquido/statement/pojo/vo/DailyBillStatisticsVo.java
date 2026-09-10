package com.liquido.statement.pojo.vo;

import java.io.Serializable;
import java.time.LocalDate;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyBillStatisticsVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

}
