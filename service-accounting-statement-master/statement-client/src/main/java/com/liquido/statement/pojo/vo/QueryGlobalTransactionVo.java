package com.liquido.statement.pojo.vo;

import java.io.Serializable;
import java.time.LocalDate;
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
public class QueryGlobalTransactionVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

}
