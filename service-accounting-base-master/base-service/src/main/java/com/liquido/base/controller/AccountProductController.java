package com.liquido.base.controller;

import java.util.List;
import javax.validation.Valid;

import com.liquido.base.api.AccountProductApi;
import com.liquido.base.pojo.dto.AccountProductDto;
import com.liquido.base.pojo.dto.AccountProductGroupDto;
import com.liquido.base.pojo.dto.PaymentLinkProductDto;
import com.liquido.base.pojo.vo.BatchAddAccountProductVo;
import com.liquido.base.pojo.vo.ListAccountProductVo;
import com.liquido.base.pojo.vo.QueryAccountProductGroupVo;
import com.liquido.base.pojo.vo.QueryByTransactionTypeVo;
import com.liquido.base.pojo.vo.QueryPaymentLinkSupportProductVo;
import com.liquido.base.service.AccountProductService;
import com.liquido.core.mvc.dto.ResponseDto;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
public class AccountProductController implements AccountProductApi {

    private final AccountProductService accountProductService;

    @Override
    @PostMapping("/base/account-product/batch-add")
    public ResponseDto<List<AccountProductDto>> batchAddAccountProduct(
            @Valid @RequestBody final BatchAddAccountProductVo vo) {
        return ResponseDto.success(accountProductService.batchAddAccountProduct(vo));
    }

    @Override
    @PostMapping("/base/account-product/list")
    public ResponseDto<List<AccountProductDto>> listAccountProduct(
            @Valid @RequestBody final ListAccountProductVo vo) {
        return ResponseDto.success(accountProductService.listAccountProduct(vo));
    }

    @Override
    @PostMapping("/base/account-product/by-transaction-type/list")
    public ResponseDto<List<AccountProductDto>> queryByTransactionType(
            @Valid @RequestBody final QueryByTransactionTypeVo vo) {
        return ResponseDto.success(
                accountProductService.findAllByTransactionTypeCode(vo.getTransactionType()));
    }

    @Override
    @PostMapping("/base/account-product/group-products/query")
    public ResponseDto<AccountProductGroupDto> queryGroupProductsOfProduct(
            @Valid @RequestBody final QueryAccountProductGroupVo vo) {
        return ResponseDto.success(accountProductService.queryGroupProductsOfProduct(vo));
    }

    @Override
    @PostMapping("/base/account-product/support/payment-link/query")
    public ResponseDto<List<PaymentLinkProductDto>> querySupportPaymentLinkProduct(
            @Valid @RequestBody final QueryPaymentLinkSupportProductVo vo) {

        final List<PaymentLinkProductDto> result =
                accountProductService.querySupportPaymentLinkProduct(vo);

        return ResponseDto.success(result);
    }
}
