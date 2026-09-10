package com.liquido.statement.service.payment.convert;

import com.liquido.base.enums.ProductCodeEnum;
import com.liquido.statement.pojo.dto.payment.BasePayoutResult;
import com.liquido.statement.pojo.dto.payment.QueryPayoutResultDto;

public interface PayoutResultConvert<T extends BasePayoutResult> {

    ProductCodeEnum getProductCode();

    QueryPayoutResultDto convertResult(T t);
}
