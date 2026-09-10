package com.liquido.statement.pojo.vo;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Convert;
import javax.validation.constraints.NotNull;

import com.liquido.statement.enums.SwitchEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillRecalculationVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull
    private List<Long> accountIdList;

    @NotNull
    @Convert(converter = SwitchEnum.Convert.class)
    private SwitchEnum switchState;

}
