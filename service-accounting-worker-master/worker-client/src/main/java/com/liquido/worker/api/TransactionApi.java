package com.liquido.worker.api;

import java.util.List;
import javax.validation.Valid;

import com.liquido.core.mvc.dto.ResponseDto;
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

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface TransactionApi {

    @PostMapping("/worker/transaction/page")
    ResponseDto<PageVo<PageTransactionDto>> pageTransaction(
            @RequestBody @Valid final PageTransactionVo vo);

    @PostMapping("/worker/transaction/advanced-page")
    ResponseDto<PageVo<PageTransactionDto>> advancedPageTransaction(
            @RequestBody @Valid final AdvancedPageTransactionVo vo);

    @PostMapping("/worker/transaction/query")
    ResponseDto<QueryTransactionDto> queryTransaction(
            @RequestBody @Valid final QueryTransactionVo vo);

    @PostMapping("/worker/transaction/metric/query")
    ResponseDto<List<MetricTransactionDto>> queryTransactionMetric(
            @RequestBody @Valid final QueryTransactionMetricVo vo);

    @PostMapping("/worker/transaction/metric/sync")
    ResponseDto<Void> syncTransactionMetric(
            @RequestBody @Valid final SyncTransactionVo vo);

    @PostMapping("/worker/transaction/ratio/sync")
    ResponseDto<Void> syncTransactionRatio(@RequestBody @Valid final SyncTransactionVo vo);


    @PostMapping("/worker/transaction/status/from-work-order/query")
    ResponseDto<List<QueryStatusByDetailDto>> queryStatusFromWorkOrder(
            @RequestBody @Valid final QueryStatusByDetailVo vo);

    @PostMapping("/worker/transaction/status/from-work-order/query-unique-id")
    ResponseDto<List<QueryStatusByUniqueIdDto>> queryStatusFromWorkOrderByUniqueId(
            @RequestBody @Valid final QueryStatusByUniqueIdVo vo);

    @PostMapping("/worker/transaction/country-ratio/list")
    ResponseDto<List<CountryTransactionRatioDto>> queryCountryTransactionRatio(
            @RequestBody @Valid final QueryCountryTransactionRatioVo vo);

    @PostMapping("/worker/transaction/merchant-ratio/product/summary")
    ResponseDto<List<TransactionRatioDto>> queryMerchantRatioSummaryByProduct(
            @RequestBody @Valid final QueryTransactionRatioVo vo);

}
