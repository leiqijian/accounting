package com.liquido.statement.pojo.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EditAccountVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull
    private Long id;

    private BigDecimal holdingLimit;

    @Length(max = 5)
    private String timezone;

    @Length(max = 32)
    private String timezoneName;

}
