package com.liquido.statement.pojo.vo;

import java.io.Serializable;
import java.util.Collection;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListAccountStatementVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotEmpty
    private Collection<Long> accountIds;

    @NotEmpty
    private Collection<Long> billIds;

    @NotBlank
    private String subMerchantId;

}
