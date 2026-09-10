package com.liquido.statement.pojo.vo;

import java.io.Serializable;
import java.util.List;
import javax.validation.Valid;
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
public class BatchAddAccountVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    private Long merchantId;

    @Valid
    @NotEmpty
    private List<AddAccountVo> listVo;

}
