package com.liquido.worker.pojo.vo;

import java.io.Serializable;
import javax.persistence.Convert;
import javax.validation.constraints.NotNull;

import com.liquido.worker.enums.DefenseStatusEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolveDefenseOrderVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull
    private String uniqueId;

    @NotNull
    @Convert(converter = DefenseStatusEnum.Convert.class)
    private DefenseStatusEnum defenseStatus;

}
