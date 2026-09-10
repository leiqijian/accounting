package com.liquido.statement.pojo.vo;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyncTransactionFxRateVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull
    private Set<Long> accountIds;

    @NotNull
    private LocalDate beginDate;

    @NotNull
    private LocalDate endDate;
}
