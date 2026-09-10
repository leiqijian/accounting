package com.liquido.base.pojo.vo;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QueryMerchantCodeVo {

    @NotBlank
    private String merchantCode;

}
