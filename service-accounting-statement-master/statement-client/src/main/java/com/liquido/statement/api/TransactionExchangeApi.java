package com.liquido.statement.api;

import javax.validation.Valid;

import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.statement.pojo.vo.OfflineExchangeVo;
import com.liquido.statement.pojo.vo.TransactionBizVo;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface TransactionExchangeApi {

    @PostMapping("/statement/account/exchange/approved/pass")
    ResponseDto<Void> exchangeApprovedPass(@RequestBody @Valid final TransactionBizVo order);

    @PostMapping("/statement/account/console/exchange/offline")
    ResponseDto<Void> offlineExchange(@RequestBody @Valid final OfflineExchangeVo order);

}
