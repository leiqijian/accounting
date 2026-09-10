package com.liquido.statement.service.payment.link;

import java.util.Collection;
import java.util.List;

import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.base.enums.ProductCodeEnum;
import com.liquido.statement.pojo.dto.PaymentLinkDto;
import com.liquido.statement.pojo.dto.payment.PaymentLinkInstallmentsPlanDto;
import com.liquido.statement.pojo.vo.CreatePaymentLinkVo;
import com.liquido.statement.pojo.vo.InstallmentsPlanCheckVo;
import com.liquido.statement.pojo.vo.QueryInstallmentsPlanVo;

public interface PaymentLinkService {

    PaymentLinkDto generatePaymentLink(final CreatePaymentLinkVo vo);

    Collection<String> convertSystemProductCodes(final Collection<ProductCodeEnum> productCodes,
                                                 final CountryCodeEnum countryCode);

    List<PaymentLinkInstallmentsPlanDto> queryInstallmentsPlan(final QueryInstallmentsPlanVo vo);

    Boolean installmentsPlanCheck(final InstallmentsPlanCheckVo vo);
}
