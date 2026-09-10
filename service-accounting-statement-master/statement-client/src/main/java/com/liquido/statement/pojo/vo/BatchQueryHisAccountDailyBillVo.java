package com.liquido.statement.pojo.vo;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchQueryHisAccountDailyBillVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotEmpty
    private Set<Long> accountIds;

    @NotNull
    private LocalDate billDate;

}
