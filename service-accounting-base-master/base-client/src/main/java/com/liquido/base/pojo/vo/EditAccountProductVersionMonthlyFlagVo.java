package com.liquido.base.pojo.vo;

import java.util.List;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditAccountProductVersionMonthlyFlagVo {

    @NotEmpty
    private List<Long> accountProductVersionIds;

    @NotNull
    private Boolean monthFlag;

}
