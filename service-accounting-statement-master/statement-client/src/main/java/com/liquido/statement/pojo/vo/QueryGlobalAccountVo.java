package com.liquido.statement.pojo.vo;

import java.io.Serializable;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryGlobalAccountVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull
    @Min(value = 1, message = "merchantId invalid")
    private Long merchantId;
}
