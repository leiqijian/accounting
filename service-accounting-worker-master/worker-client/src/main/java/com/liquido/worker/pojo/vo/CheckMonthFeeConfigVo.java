package com.liquido.worker.pojo.vo;

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
public class CheckMonthFeeConfigVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    private LocalDate activeDate;

    @NotNull
    private String timezone;
}
