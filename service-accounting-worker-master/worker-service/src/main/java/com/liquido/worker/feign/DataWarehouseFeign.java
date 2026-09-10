package com.liquido.worker.feign;

import javax.validation.Valid;

import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.worker.aop.DataWarehouseFeignAdvice;
import com.liquido.worker.feign.config.DataWarehouseConfiguration;
import com.liquido.worker.pojo.bo.DwPage;
import com.liquido.worker.pojo.bo.DwResponse;
import com.liquido.worker.pojo.dto.DwMetricSuccessRateDto;
import com.liquido.worker.pojo.dto.DwMetricTransactionDto;
import com.liquido.worker.pojo.dto.DwPageTransactionDto;
import com.liquido.worker.pojo.dto.DwQueryTransactionDto;
import com.liquido.worker.pojo.dto.DwSyncPaymentLinkDto;
import com.liquido.worker.pojo.dto.DwSyncShopifyDto;
import com.liquido.worker.pojo.dto.DwSyncShoplazzaDto;
import com.liquido.worker.pojo.dto.DwSyncTokenizationDto;
import com.liquido.worker.pojo.dto.DwSyncTransactionDto;
import com.liquido.worker.pojo.vo.DwAdvancedQueryTransactionVo;
import com.liquido.worker.pojo.vo.DwPageTransactionVo;
import com.liquido.worker.pojo.vo.DwQueryTransactionVo;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@Import(DataWarehouseFeignAdvice.class)
@FeignClient(name = "data-warehouse", url = "https://this-is-a-placeholder.com",
        fallbackFactory = DataWarehouseConfiguration.class,
        configuration = DataWarehouseConfiguration.class)
public interface DataWarehouseFeign {

    /**
     * Get the page of transaction data
     *
     * @param vo request params
     * @return page of transaction data
     */
    @GetMapping("/transaction/")
    DwResponse<DwPage<DwPageTransactionDto>> pageTransaction(
            @RequestHeader("Env") final TransactionTypeCodeEnum env,
            @RequestHeader("Try-All-Env-Flag") final Boolean tryAllEnvFlag,
            final DwPageTransactionVo vo);

    /**
     * Get the page of transaction data with advanced query
     *
     * @param vo request params
     * @return list of unique id
     */
    @PostMapping("/transaction/advancedQuery")
    DwResponse<DwPage<String>> advancedPageTransaction(
            @RequestHeader("Env") final TransactionTypeCodeEnum env,
            @RequestHeader("Try-All-Env-Flag") final Boolean tryAllEnvFlag,
            @RequestBody @Valid final DwAdvancedQueryTransactionVo vo);

    /**
     * Get unique id from subAccountId and trackingId
     *
     * @param subAccountId subAccountId
     * @param trackingId   trackingId
     * @return unique id
     */
    @GetMapping("/transaction/getUniqueId")
    DwResponse<String> queryUniqueId(
            @RequestHeader("Env") final TransactionTypeCodeEnum env,
            @RequestHeader("Try-All-Env-Flag") final Boolean tryAllEnvFlag,
            @RequestParam(value = "merchantCode") final String merchantCode,
            @RequestParam(value = "subAccountId") final String subAccountId,
            @RequestParam(value = "trackingId") final String trackingId);

    /**
     * Get the detail info of the transaction data
     *
     * @param vo params
     * @return detail info
     */
    @GetMapping("/transaction/detail")
    DwResponse<DwQueryTransactionDto> queryTransaction(
            @RequestHeader("Env") final TransactionTypeCodeEnum env,
            @RequestHeader("Try-All-Env-Flag") final Boolean tryAllEnvFlag,
            final DwQueryTransactionVo vo);

    /**
     * Get the count and sum amount of the transaction per 15 min
     *
     * @param from from UTC
     * @param to   to UTC
     * @return metric data
     */
    @GetMapping("/metric/transaction")
    DwResponse<DwPage<DwMetricTransactionDto>> metricTransaction(
            @RequestHeader("Env") final TransactionTypeCodeEnum env,
            @RequestHeader("Try-All-Env-Flag") final Boolean tryAllEnvFlag,
            @RequestParam(value = "from") final Long from,
            @RequestParam(value = "to") final Long to);


    /**
     * Get the success rate of the transaction per 15 min
     *
     * @param from from UTC
     * @param to   to UTC
     * @return metric data
     */
    @GetMapping("/metric/success_rate")
    DwResponse<DwPage<DwMetricSuccessRateDto>> metricSuccessRate(
            @RequestHeader("Env") final TransactionTypeCodeEnum env,
            @RequestHeader("Try-All-Env-Flag") final Boolean tryAllEnvFlag,
            @RequestParam(value = "from") final Long from,
            @RequestParam(value = "to") final Long to);

    /**
     * Get the transaction data, considering the large flow,
     * worker service direct docking this interface,
     * the transaction service don't use this interface.
     *
     * @param from from of timestamp
     * @param to   to of timestamp
     * @return transaction data
     */
    @GetMapping("/event_log/")
    DwResponse<DwPage<DwSyncTransactionDto>> getTransactionData(
            @RequestHeader("Env") final TransactionTypeCodeEnum env,
            @RequestHeader("Try-All-Env-Flag") final Boolean tryAllEnvFlag,
            @RequestParam(value = "from") final Long from,
            @RequestParam(value = "to") final Long to);

    /**
     * Get the transaction data, considering the large flow,
     * worker service direct docking this interface,
     * the transaction service don't use this interface.
     *
     * @param from from of timestamp
     * @param to   to of timestamp
     * @return transaction data
     */
    @GetMapping("/event_log/list")
    DwResponse<DwPage<DwSyncTransactionDto>> getTransactionDataByObjectIdTime(
            @RequestHeader("Env") final TransactionTypeCodeEnum env,
            @RequestHeader("Try-All-Env-Flag") final Boolean tryAllEnvFlag,
            @RequestParam(value = "from") final Long from,
            @RequestParam(value = "to") final Long to);

    /**
     * Get the payment link data, considering the large flow,
     * worker service direct docking this interface,
     * the transaction service don't use this interface.
     *
     * @param from from of timestamp
     * @param to   to of timestamp
     * @return payment link data
     */
    @GetMapping("/event_log/payment_link")
    DwResponse<DwPage<DwSyncPaymentLinkDto>> getPaymentLinkDataByObjectIdTime(
            @RequestHeader("Env") final TransactionTypeCodeEnum env,
            @RequestHeader("Try-All-Env-Flag") final Boolean tryAllEnvFlag,
            @RequestParam(value = "from") final Long from,
            @RequestParam(value = "to") final Long to);

    /**
     * Get the shopify data, considering the large flow,
     * worker service direct docking this interface,
     * the transaction service don't use this interface.
     *
     * @param from from of timestamp
     * @param to   to of timestamp
     * @return shopify data
     */
    @GetMapping("/event_log/shopify")
    DwResponse<DwPage<DwSyncShopifyDto>> getShopifyDataByObjectIdTime(
            @RequestHeader("Env") final TransactionTypeCodeEnum env,
            @RequestHeader("Try-All-Env-Flag") final Boolean tryAllEnvFlag,
            @RequestParam(value = "from") final Long from,
            @RequestParam(value = "to") final Long to);

    /**
     * Get the shoplazza data, considering the large flow,
     * worker service direct docking this interface,
     * the transaction service don't use this interface.
     *
     * @param from from of timestamp
     * @param to   to of timestamp
     * @return shoplazza data
     */
    @GetMapping("/event_log/shoplazza")
    DwResponse<DwPage<DwSyncShoplazzaDto>> getShoplazzaDataByObjectIdTime(
            @RequestHeader("Env") final TransactionTypeCodeEnum env,
            @RequestHeader("Try-All-Env-Flag") final Boolean tryAllEnvFlag,
            @RequestParam(value = "from") final Long from,
            @RequestParam(value = "to") final Long to);

    @GetMapping("/event_log/token")
    DwResponse<DwPage<DwSyncTokenizationDto>> getTokenDataByObjectIdTime(
            @RequestHeader("Env") final TransactionTypeCodeEnum env,
            @RequestHeader("Try-All-Env-Flag") final Boolean tryAllEnvFlag,
            @RequestParam(value = "from") final Long from,
            @RequestParam(value = "to") final Long to);

}
