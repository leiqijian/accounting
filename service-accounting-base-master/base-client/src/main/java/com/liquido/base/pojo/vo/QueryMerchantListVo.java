package com.liquido.base.pojo.vo;


import java.io.Serializable;
import java.util.Set;
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
public class QueryMerchantListVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull
    @NotEmpty
    private Set<Long> merchantIds;

}
