package com.liquido.statement.controller;

import javax.validation.Valid;

import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.statement.api.TransactionExchangeApi;
import com.liquido.statement.manage.BizExchangeManager;
import com.liquido.statement.pojo.vo.OfflineExchangeVo;
import com.liquido.statement.pojo.vo.TransactionBizVo;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
public class TransactionExchangeController implements TransactionExchangeApi {
    private final BizExchangeManager bizExchangeManager;

    /**
     * Api for merchant dashboard withdraw approved pass(Approval Exchange)
     * /statement/account/approval/apply
     *
     * @param order vo
     */
    @Override
    @PostMapping("/statement/account/exchange/approved/pass")
    public ResponseDto<Void> exchangeApprovedPass(
            @RequestBody @Valid final TransactionBizVo order) {
        bizExchangeManager.approvedPass(order);
        return ResponseDto.success();
    }

    @Override
    @PostMapping("/statement/account/console/exchange/offline")
    public ResponseDto<Void> offlineExchange(@RequestBody @Valid final OfflineExchangeVo vo) {
        bizExchangeManager.offlineExchange(vo);
        return ResponseDto.success();
    }

}
