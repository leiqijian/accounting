package com.liquido.base.pojo.vo;

import java.io.Serializable;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuerySubMerchantVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String subMerchantId;

    @NotNull
    private Long merchantId;
}
