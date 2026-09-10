package com.liquido.statement.pojo.vo;

import java.io.Serializable;
import java.util.Set;
import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchAddSubAccountVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank
    private String merchantCode;

    private Set<String> subMerchantIds;

}
