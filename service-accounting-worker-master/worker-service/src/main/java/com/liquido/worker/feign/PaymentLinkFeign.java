package com.liquido.worker.feign;

import com.liquido.worker.feign.config.PaymentLinkConfiguration;
import com.liquido.worker.pojo.bo.PaymentLinkResponse;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "payment-link", url = "${worker.payment-link.url}",
        fallbackFactory = PaymentLinkConfiguration.class)
public interface PaymentLinkFeign {

    /**
     * Get the detail of refund data
     *
     * @param linkId request params
     * @return detail data
     */
    @GetMapping("/payment-link/{linkId}/refund_addition_info")
    PaymentLinkResponse<JsonNode> queryRefund(final @PathVariable String linkId);

}
