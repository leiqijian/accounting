package com.liquido.base.api;

import java.util.List;
import javax.validation.Valid;

import com.liquido.base.pojo.dto.AccountProductDto;
import com.liquido.base.pojo.dto.AccountProductGroupDto;
import com.liquido.base.pojo.dto.PaymentLinkProductDto;
import com.liquido.base.pojo.vo.BatchAddAccountProductVo;
import com.liquido.base.pojo.vo.ListAccountProductVo;
import com.liquido.base.pojo.vo.QueryAccountProductGroupVo;
import com.liquido.base.pojo.vo.QueryByTransactionTypeVo;
import com.liquido.base.pojo.vo.QueryPaymentLinkSupportProductVo;
import com.liquido.core.mvc.dto.ResponseDto;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface AccountProductApi {

    @PostMapping("/base/account-product/batch-add")
    ResponseDto<List<AccountProductDto>> batchAddAccountProduct(
            @Valid @RequestBody BatchAddAccountProductVo vo);

    @PostMapping("/base/account-product/list")
    ResponseDto<List<AccountProductDto>> listAccountProduct(
            @Valid @RequestBody final ListAccountProductVo vo);

    @PostMapping("/base/account-product/by-transaction-type/list")
    ResponseDto<List<AccountProductDto>> queryByTransactionType(
            @Valid @RequestBody final QueryByTransactionTypeVo vo);

    @PostMapping("/base/account-product/group-products/query")
    ResponseDto<AccountProductGroupDto> queryGroupProductsOfProduct(
            @Valid @RequestBody final QueryAccountProductGroupVo vo);

    @PostMapping("/base/account-product/support/payment-link/query")
    ResponseDto<List<PaymentLinkProductDto>> querySupportPaymentLinkProduct(
            @Valid @RequestBody final QueryPaymentLinkSupportProductVo vo);
}
