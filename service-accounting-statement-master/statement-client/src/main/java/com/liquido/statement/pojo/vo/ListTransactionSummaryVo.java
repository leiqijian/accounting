package com.liquido.statement.pojo.vo;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListTransactionSummaryVo implements Serializable {
    private static final long serialVersionUID = 1L;

    private LocalDate startDate;

    private LocalDate endDate;

    @NotEmpty
    private List<Long> accountIds;
}
