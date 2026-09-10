package com.liquido.base.pojo.vo;

import java.io.Serializable;
import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DynamicConstantCreateMonitorVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank
    private String classSimpleName;

    @NotBlank
    private String code;
}
