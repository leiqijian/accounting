package com.liquido.statement.pojo.dto.payment;

import java.io.Serializable;

import com.liquido.base.enums.ProductCodeEnum;

public interface BasePayoutResult extends Serializable {

    ProductCodeEnum getProductCode();

}
