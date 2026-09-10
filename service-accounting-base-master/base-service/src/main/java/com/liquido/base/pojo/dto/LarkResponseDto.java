package com.liquido.base.pojo.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LarkResponseDto<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer code;

    private T data;

    private String msg;

}
