package com.liquido.base.pojo.vo;

import java.io.Serializable;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchAddProductFeeConfigsVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Valid
    @NotEmpty
    private List<AccountFeeConfigurationVo> listVo;

}
