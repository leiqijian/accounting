package com.liquido.worker;

import com.liquido.worker.api.DefenseOrderApi;
import com.liquido.worker.api.MonthFeeConfigGenerationApi;
import com.liquido.worker.api.PaymentLinkApi;
import com.liquido.worker.api.ShopifyApi;
import com.liquido.worker.api.ShoplazzaApi;
import com.liquido.worker.api.TaskFeeCalculationApi;
import com.liquido.worker.api.TokenizationApi;
import com.liquido.worker.api.TransactionApi;
import com.liquido.worker.api.TransactionProofApi;

import org.springframework.cloud.openfeign.FeignClient;

public interface WorkerApis extends MonthFeeConfigGenerationApi, PaymentLinkApi,
        ShopifyApi, ShoplazzaApi, TaskFeeCalculationApi, TokenizationApi, TransactionApi,
        TransactionProofApi,
        DefenseOrderApi {

    @FeignClient("${worker.feign.name:service-accounting-worker}")
    interface WorkerFeign extends WorkerApis {
    }
}
