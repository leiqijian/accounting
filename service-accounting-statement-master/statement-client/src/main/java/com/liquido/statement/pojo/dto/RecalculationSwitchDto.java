package com.liquido.statement.pojo.dto;

import java.io.Serializable;
import javax.persistence.Convert;

import com.liquido.statement.enums.SwitchEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecalculationSwitchDto implements Serializable {
    private static final long serialVersionUID = 1L;

    @Convert(converter = SwitchEnum.Convert.class)
    private SwitchEnum switchState;

}
