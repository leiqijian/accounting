package com.liquido.worker.controller;

import java.util.List;
import javax.validation.Valid;

import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.core.mvc.vo.PageVo;
import com.liquido.worker.api.TransactionApi;
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
import com.liquido.worker.service.TransactionService;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
public class TransactionController implements TransactionApi {

    private final TransactionService transactionService;

    @Override
    @PostMapping("/worker/transaction/page")
    public ResponseDto<PageVo<PageTransactionDto>> pageTransaction(
            @RequestBody @Valid final PageTransactionVo vo) {
        return ResponseDto.success(transactionService.pageTransaction(vo));
    }

    @Override
    @PostMapping("/worker/transaction/advanced-page")
    public ResponseDto<PageVo<PageTransactionDto>> advancedPageTransaction(
            @RequestBody @Valid final AdvancedPageTransactionVo vo) {
        return ResponseDto.success(transactionService.advancedPageTransaction(vo));
    }

    @Override
    @PostMapping("/worker/transaction/query")
    public ResponseDto<QueryTransactionDto> queryTransaction(
            @RequestBody @Valid QueryTransactionVo vo) {
        return ResponseDto.success(transactionService.queryTransaction(vo));
    }

    @Override
    @PostMapping("/worker/transaction/metric/query")
    public ResponseDto<List<MetricTransactionDto>> queryTransactionMetric(
            @RequestBody @Valid final QueryTransactionMetricVo vo) {
        return ResponseDto.success(transactionService.queryTransactionMetric(vo));
    }

    @Override
    @PostMapping("/worker/transaction/metric/sync")
    public ResponseDto<Void> syncTransactionMetric(@RequestBody @Valid final SyncTransactionVo vo) {
        transactionService.syncTransactionMetric(vo);
        return ResponseDto.success();
    }

    @Override
    @PostMapping("/worker/transaction/ratio/sync")
    public ResponseDto<Void> syncTransactionRatio(@RequestBody @Valid final SyncTransactionVo vo) {
        transactionService.syncTransactionRatio(vo);
        return ResponseDto.success();
    }

    @Override
    @PostMapping("/worker/transaction/status/from-work-order/query")
    public ResponseDto<List<QueryStatusByDetailDto>> queryStatusFromWorkOrder(
            @RequestBody @Valid final QueryStatusByDetailVo vo) {
        return ResponseDto.success(transactionService.queryStatusByDetail(vo));
    }

    @Override
    @PostMapping("/worker/transaction/status/from-work-order/query-unique-id")
    public ResponseDto<List<QueryStatusByUniqueIdDto>> queryStatusFromWorkOrderByUniqueId(
            @RequestBody @Valid final QueryStatusByUniqueIdVo vo) {
        return ResponseDto.success(transactionService.queryStatusByUniqueId(vo));
    }

    @Override
    @PostMapping("/worker/transaction/country-ratio/list")
    public ResponseDto<List<CountryTransactionRatioDto>> queryCountryTransactionRatio(
            @RequestBody @Valid final QueryCountryTransactionRatioVo vo) {
        return ResponseDto.success(transactionService.queryCountryTransactionRatio(vo));
    }

    @Override
    @PostMapping("/worker/transaction/merchant-ratio/product/summary")
    public ResponseDto<List<TransactionRatioDto>> queryMerchantRatioSummaryByProduct(
            @RequestBody @Valid final QueryTransactionRatioVo vo) {
        return ResponseDto.success(transactionService.queryMerchantRatioSummaryByProduct(vo));
    }

}
