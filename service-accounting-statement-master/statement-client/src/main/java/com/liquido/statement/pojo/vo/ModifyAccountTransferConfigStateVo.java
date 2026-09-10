package com.liquido.statement.pojo.vo;

import java.io.Serializable;
import javax.persistence.Convert;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.liquido.statement.enums.TransferConfigStateEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModifyAccountTransferConfigStateVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @Min(1)
    @NotNull
    private Long id;

    @NotNull
    @Convert(converter = TransferConfigStateEnum.Convert.class)
    private TransferConfigStateEnum state;

}
