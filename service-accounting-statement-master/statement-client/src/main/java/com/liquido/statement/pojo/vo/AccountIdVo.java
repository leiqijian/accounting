package com.liquido.statement.pojo.vo;

import java.io.Serializable;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountIdVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * accountId
     */
    @Min(1)
    @NotNull
    private Long accountId;
}
