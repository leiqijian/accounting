package com.liquido.statement.pojo.vo;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HandleSubAccountDailyCutVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotEmpty
    private Set<Long> subAccountIdList;

    @NotNull
    private LocalDate beginDate;

    @NotNull
    private LocalDate endDate;

}
