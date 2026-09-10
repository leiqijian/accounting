package com.liquido.base.service;

import java.util.Collection;

import com.liquido.base.pojo.dto.BatchPaymentConfigCreateDto;
import com.liquido.base.pojo.dto.PaymentConfigDto;
import com.liquido.base.pojo.vo.BatchCreatePaymentLinkPaymentConfigVo;
import com.liquido.base.pojo.vo.BatchCreatePayoutPaymentConfigVo;
import com.liquido.base.pojo.vo.QueryPaymentConfigVo;

public interface PaymentConfigService {

    BatchPaymentConfigCreateDto batchCreatePayoutPaymentConfig(
            final BatchCreatePayoutPaymentConfigVo vo);

    BatchPaymentConfigCreateDto batchCreatePaymentLinkPaymentConfig(
            final BatchCreatePaymentLinkPaymentConfigVo vo);

    boolean existsByAccountIds(final Collection<Long> accountIds);

    PaymentConfigDto queryPaymentConfig(final QueryPaymentConfigVo vo);

    PaymentConfigDto queryPaymentConfig(final Long configId);
}
