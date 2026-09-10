package com.liquido.base.pojo.vo;

import java.io.Serializable;
import java.time.LocalDate;
import javax.validation.constraints.Past;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryPeriodTimeVo implements Serializable {
    private static final long serialVersionUID = -69726081514592568L;

    @Past
    private LocalDate startDate;

    @Past
    private LocalDate endDate;

}
