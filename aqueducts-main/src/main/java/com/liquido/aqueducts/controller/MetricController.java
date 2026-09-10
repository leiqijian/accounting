package com.liquido.aqueducts.controller;

import com.liquido.aqueducts.commons.PageResult;
import com.liquido.aqueducts.commons.ResponseData;
import com.liquido.aqueducts.commons.enums.ResultCode;
import com.liquido.aqueducts.service.MetricService;
import com.liquido.aqueducts.vo.response.TransactionMetricItem;
import com.liquido.aqueducts.vo.response.TransactionCountItem;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/metric")
@Slf4j
public class MetricController {

    @Autowired
    private MetricService metricService;

    @GetMapping("/transaction")
    public ResponseEntity<ResponseData<PageResult<TransactionMetricItem>>> list(
            @RequestParam(value = "from") final long from,
            @RequestParam(value = "to") final long to
    ) {
        PageResult<TransactionMetricItem> page = metricService.list(from, to);
        return new ResponseEntity<>(new ResponseData<>(ResultCode.SUCCESS, page), HttpStatus.OK);
    }

    @GetMapping("/success_rate")
    public ResponseEntity<ResponseData<PageResult<TransactionCountItem>>> rate(
            @RequestParam(value = "from") final long from,
            @RequestParam(value = "to") final long to
    ) {
        PageResult<TransactionCountItem> page = metricService.rate(from, to);
        return new ResponseEntity<>(new ResponseData<>(ResultCode.SUCCESS, page), HttpStatus.OK);
    }

}
