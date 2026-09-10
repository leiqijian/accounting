package com.liquido.worker.pojo.bo;

import java.io.Serializable;

import com.liquido.base.enums.ProductCodeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountProductKey implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long accountId;

    private ProductCodeEnum productCode;
}
