package com.liquido.worker.service;

import java.util.List;

import com.liquido.core.mvc.vo.PageVo;
import com.liquido.worker.pojo.dto.PagePaymentLinkDto;
import com.liquido.worker.pojo.dto.QueryPaymentLinkDto;
import com.liquido.worker.pojo.vo.BatchQueryPaymentLinkVo;
import com.liquido.worker.pojo.vo.PagePaymentLinkVo;
import com.liquido.worker.pojo.vo.QueryPaymentLinkVo;

public interface PaymentLinkService {

    PageVo<PagePaymentLinkDto> pagePaymentLink(final PagePaymentLinkVo vo);

    QueryPaymentLinkDto queryPaymentLink(final QueryPaymentLinkVo vo);

    List<String> queryExistsPaymentLinkOrderIds(final BatchQueryPaymentLinkVo vo);

}
