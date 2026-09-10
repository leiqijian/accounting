package com.liquido.transaction.api;


import java.util.List;
import javax.validation.Valid;

import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.core.mvc.vo.PageVo;
import com.liquido.transaction.pojo.bo.ApprovalBizExchangeConfigBo;
import com.liquido.transaction.pojo.dto.ApprovalBizExchangeDto;
import com.liquido.transaction.pojo.dto.CalculateExchangeAmountDto;
import com.liquido.transaction.pojo.dto.QueryApprovalBizExchangeDto;
import com.liquido.transaction.pojo.dto.QueryApprovalConfigDto;
import com.liquido.transaction.pojo.vo.CalculateExchangeAmountVo;
import com.liquido.transaction.pojo.vo.CreateApprovalBizExchangeVo;
import com.liquido.transaction.pojo.vo.CreateApprovalConfigVo;
import com.liquido.transaction.pojo.vo.PageApprovalBizExchangeVo;
import com.liquido.transaction.pojo.vo.QueryApprovalBizExchangeVo;
import com.liquido.transaction.pojo.vo.QueryApprovalConfigVo;
import com.liquido.transaction.pojo.vo.SubmitOfflineProofBizExchangeVo;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface ApprovalBizExchangeApi {

    @PostMapping("/transaction/approval/exchange/config/query")
    ResponseDto<QueryApprovalConfigDto<ApprovalBizExchangeConfigBo>> queryApprovalExchangeConfig(
            @RequestBody @Valid final QueryApprovalConfigVo vo);

    @PostMapping("/transaction/approval/exchange/create")
    ResponseDto<ApprovalBizExchangeDto> createApprovalBizExchange(
            @RequestBody @Valid final CreateApprovalBizExchangeVo vo);

    @PostMapping("/transaction/approval/exchange/offline/proof/submit")
    ResponseDto<Void> submitOfflineProofBizExchange(
            @RequestBody @Valid final SubmitOfflineProofBizExchangeVo vo);

    @PostMapping("/transaction/approval/exchange/page")
    ResponseDto<PageVo<ApprovalBizExchangeDto>> pageApprovalBizExchange(
            @RequestBody @Valid final PageApprovalBizExchangeVo vo);

    @PostMapping("/transaction/approval/exchange/query")
    ResponseDto<QueryApprovalBizExchangeDto> queryApprovalBizExchange(
            @RequestBody @Valid final QueryApprovalBizExchangeVo vo);

    @PostMapping("/transaction/approval/exchange/cancel")
    ResponseDto<Void> cancelApprovalBizExchange(
            @RequestBody @Valid final QueryApprovalBizExchangeVo vo);

    @PostMapping("/transaction/approval/exchange/contract/confirm/{exchangeApprovalId}")
    ResponseDto<Void> contractConfirm(@PathVariable final Long exchangeApprovalId);

    @PostMapping("/transaction/approval/exchange/calculate")
    ResponseDto<CalculateExchangeAmountDto> calculateExchangeAmount(
            @RequestBody @Valid final CalculateExchangeAmountVo vo);

    @PostMapping("/transaction/approval/exchange/config/create/batch")
    ResponseDto<List<QueryApprovalConfigDto<ApprovalBizExchangeConfigBo>>>
    createExchangeConfigBatch(@RequestBody @Valid
                              final List<CreateApprovalConfigVo<ApprovalBizExchangeConfigBo>> list);

}
