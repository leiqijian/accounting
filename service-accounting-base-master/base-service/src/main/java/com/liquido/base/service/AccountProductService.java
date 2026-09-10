package com.liquido.base.service;

import java.util.List;

import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.base.pojo.dto.AccountProductDto;
import com.liquido.base.pojo.dto.AccountProductGroupDto;
import com.liquido.base.pojo.dto.PaymentLinkProductDto;
import com.liquido.base.pojo.vo.BatchAddAccountProductVo;
import com.liquido.base.pojo.vo.ListAccountProductVo;
import com.liquido.base.pojo.vo.QueryAccountProductGroupVo;
import com.liquido.base.pojo.vo.QueryPaymentLinkSupportProductVo;

public interface AccountProductService {

    List<AccountProductDto> batchAddAccountProduct(final BatchAddAccountProductVo vo);

    List<AccountProductDto> listAccountProduct(final ListAccountProductVo vo);

    List<AccountProductDto> findAllByTransactionTypeCode(final TransactionTypeCodeEnum code);

    AccountProductGroupDto queryGroupProductsOfProduct(final QueryAccountProductGroupVo vo);

    List<PaymentLinkProductDto> querySupportPaymentLinkProduct(
            final QueryPaymentLinkSupportProductVo vo);

}
