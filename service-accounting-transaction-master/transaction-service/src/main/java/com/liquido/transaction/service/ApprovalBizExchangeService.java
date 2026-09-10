package com.liquido.transaction.service;


import java.util.List;

import com.liquido.core.mvc.vo.PageVo;
import com.liquido.transaction.pojo.bo.ApprovalBizExchangeConfigBo;
import com.liquido.transaction.pojo.dto.ApprovalBizExchangeDto;
import com.liquido.transaction.pojo.dto.CalculateExchangeAmountDto;
import com.liquido.transaction.pojo.dto.QueryApprovalBizExchangeDto;
import com.liquido.transaction.pojo.dto.QueryApprovalConfigDto;
import com.liquido.transaction.pojo.entity.ApprovalBizExchange;
import com.liquido.transaction.pojo.vo.CalculateExchangeAmountVo;
import com.liquido.transaction.pojo.vo.CreateApprovalBizExchangeVo;
import com.liquido.transaction.pojo.vo.CreateApprovalConfigVo;
import com.liquido.transaction.pojo.vo.PageApprovalBizExchangeVo;
import com.liquido.transaction.pojo.vo.QueryApprovalBizExchangeVo;
import com.liquido.transaction.pojo.vo.SubmitOfflineProofBizExchangeVo;

public interface ApprovalBizExchangeService {

    ApprovalBizExchange findById(final Long id);

    ApprovalBizExchange findByApprovalId(final Long approvalId);

    ApprovalBizExchange findByIdAndMerchantId(final Long id, final Long merchantId);

    QueryApprovalConfigDto<ApprovalBizExchangeConfigBo> queryApprovalConfig(final Long accountId);

    ApprovalBizExchangeDto createApprovalBizExchange(
            final CreateApprovalBizExchangeVo vo);

    void submitOfflineProofBizExchange(final SubmitOfflineProofBizExchangeVo vo);

    PageVo<ApprovalBizExchangeDto> pageApprovalBizExchange(final PageApprovalBizExchangeVo vo);

    QueryApprovalBizExchangeDto queryApprovalBizExchange(final QueryApprovalBizExchangeVo vo);

    void cancelApprovalBizExchange(final QueryApprovalBizExchangeVo vo,
                                   final boolean doActionFlag);

    void contractConfirm(final Long exchangeApprovalId);

    CalculateExchangeAmountDto calculateExchangeAmount(final CalculateExchangeAmountVo vo);

    List<QueryApprovalConfigDto<ApprovalBizExchangeConfigBo>> createApprovalConfigBatch(
            final List<CreateApprovalConfigVo<ApprovalBizExchangeConfigBo>> list);

}
