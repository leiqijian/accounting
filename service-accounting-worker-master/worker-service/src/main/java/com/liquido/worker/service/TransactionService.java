package com.liquido.worker.service;

import java.util.List;

import com.liquido.core.mvc.vo.PageVo;
import com.liquido.worker.pojo.dto.CountryTransactionRatioDto;
import com.liquido.worker.pojo.dto.MetricTransactionDto;
import com.liquido.worker.pojo.dto.PageTransactionDto;
import com.liquido.worker.pojo.dto.QueryStatusByDetailDto;
import com.liquido.worker.pojo.dto.QueryStatusByUniqueIdDto;
import com.liquido.worker.pojo.dto.QueryTransactionDto;
import com.liquido.worker.pojo.dto.TransactionRatioDto;
import com.liquido.worker.pojo.vo.AdvancedPageTransactionVo;
import com.liquido.worker.pojo.vo.PageTransactionVo;
import com.liquido.worker.pojo.vo.QueryCountryTransactionRatioVo;
import com.liquido.worker.pojo.vo.QueryStatusByDetailVo;
import com.liquido.worker.pojo.vo.QueryStatusByUniqueIdVo;
import com.liquido.worker.pojo.vo.QueryTransactionMetricVo;
import com.liquido.worker.pojo.vo.QueryTransactionRatioVo;
import com.liquido.worker.pojo.vo.QueryTransactionVo;
import com.liquido.worker.pojo.vo.SyncTransactionVo;

public interface TransactionService {

    PageVo<PageTransactionDto> pageTransaction(final PageTransactionVo vo);

    PageVo<PageTransactionDto> advancedPageTransaction(final AdvancedPageTransactionVo vo);

    QueryTransactionDto queryTransaction(final QueryTransactionVo vo);

    List<MetricTransactionDto> queryTransactionMetric(final QueryTransactionMetricVo vo);

    void syncTransactionMetric(final SyncTransactionVo vo);

    List<QueryStatusByDetailDto> queryStatusByDetail(final QueryStatusByDetailVo vo);

    List<QueryStatusByUniqueIdDto> queryStatusByUniqueId(final QueryStatusByUniqueIdVo vo);

    void syncTransactionRatio(final SyncTransactionVo vo);

    List<CountryTransactionRatioDto> queryCountryTransactionRatio(
            final QueryCountryTransactionRatioVo vo);

    List<TransactionRatioDto> queryMerchantRatioSummaryByProduct(
            final QueryTransactionRatioVo vo);
}
