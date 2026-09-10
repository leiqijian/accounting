package com.liquido.statement.service;

import java.util.List;

import com.liquido.core.mvc.vo.PageVo;
import com.liquido.statement.pojo.dto.ChargeBackSummaryOrderDto;
import com.liquido.statement.pojo.dto.TransactionChargeBackOrderDto;
import com.liquido.statement.pojo.entity.TransactionChargeBackOrder;
import com.liquido.statement.pojo.vo.AcceptTransactionChargeBackOrderVo;
import com.liquido.statement.pojo.vo.ChargeBackSummaryOrderVo;
import com.liquido.statement.pojo.vo.DefenseTransactionChargeBackOrderVo;
import com.liquido.statement.pojo.vo.PageTransactionChargeBackOrderVo;
import com.liquido.statement.pojo.vo.QueryTransactionChargeBackOrderVo;
import com.liquido.statement.pojo.vo.SolveTransactionChargeBackOrderVo;

public interface TransactionChargeBackOrderService {

    ChargeBackSummaryOrderDto chargeBackSummary(final ChargeBackSummaryOrderVo vo);

    PageVo<TransactionChargeBackOrderDto> chargeBackPage(final PageTransactionChargeBackOrderVo vo);

    TransactionChargeBackOrderDto chargeBackQuery(final QueryTransactionChargeBackOrderVo vo);

    TransactionChargeBackOrder getById(final Long id);

    TransactionChargeBackOrder getByIdAndMerchantId(final Long id, final Long merchantId);

    void save(final List<TransactionChargeBackOrder> list);

    TransactionChargeBackOrderDto chargeBackDefense(DefenseTransactionChargeBackOrderVo vo);

    void chargeBackDefenseSolve(final SolveTransactionChargeBackOrderVo vo);

    void chargeBackAccept(final AcceptTransactionChargeBackOrderVo vo);

}
