package com.liquido.aqueducts.controller;

import java.util.List;

import com.liquido.aqueducts.commons.PageResult;
import com.liquido.aqueducts.commons.ResponseData;
import com.liquido.aqueducts.commons.enums.CountryCode;
import com.liquido.aqueducts.commons.enums.ProductCode;
import com.liquido.aqueducts.commons.enums.ResultCode;
import com.liquido.aqueducts.commons.enums.TransactionType;
import com.liquido.aqueducts.commons.exception.BusinessException;
import com.liquido.aqueducts.service.TransactionService;
import com.liquido.aqueducts.vo.request.TransactionAdvanceQueryRequest;
import com.liquido.aqueducts.vo.request.TransactionDetailRequest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/transaction")
@Slf4j
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @GetMapping("/detail")
    ResponseEntity<ResponseData<Object>> detail(
            @RequestParam(value = "countryCode") final String countryCode,
            @RequestParam(value = "productCode") final String productCode,
            @RequestParam(value = "merchant") final String merchant,
            @RequestParam(value = "transactionType") final String transactionType,
            @RequestParam(value = "uniqueId") final String uniqueId
    ) {
        // validation
        TransactionDetailRequest transactionDetailRequest = getValidatedTransactionDetailRequest(
                countryCode, productCode, merchant, transactionType, uniqueId
        );

        final Object data = transactionService.detail(transactionDetailRequest);

        return new ResponseEntity<>(new ResponseData<>(ResultCode.SUCCESS, data), HttpStatus.OK);
    }

    private TransactionDetailRequest getValidatedTransactionDetailRequest(
            final String countryCode,
            final String productCode,
            final String merchant,
            final String transactionType,
            final String uniqueId) {
        if (!CountryCode.isValid(countryCode)) {
            throw new BusinessException(ResultCode.INVALID_PARAMETERS.getCode(),
                    "invalid country code: " + countryCode);
        }
        if (!TransactionType.isValid(transactionType)) {
            throw new BusinessException(ResultCode.INVALID_PARAMETERS.getCode(),
                    "invalid transaction type: " + transactionType);
        }
        if (!ProductCode.isValid(productCode)) {
            throw new BusinessException(ResultCode.INVALID_PARAMETERS.getCode(),
                    "invalid product code: " + productCode);
        }
        if (!StringUtils.hasText(merchant)) {
            throw new BusinessException(ResultCode.INVALID_PARAMETERS.getCode(),
                    "merchant is null");
        }
        return TransactionDetailRequest.builder()
                .countryCode(CountryCode.fromString(countryCode))
                .productCode(ProductCode.fromString(productCode))
                .transactionType(TransactionType.fromString(transactionType))
                .merchant(merchant)
                .uniqueId(uniqueId)
                .build();
    }

    /**
     * Advanced query interface. Currently, only PIX, SPEI_VA, SPEI_BANK_TRANSFER, SPEI query is supported
     *
     * @param request
     * @return
     */
    @PostMapping("/advancedQuery")
    public ResponseEntity<ResponseData<PageResult>> advancedQuery(
            @RequestBody TransactionAdvanceQueryRequest request) {
        if (request.getOther() == null || request.getOther().isEmpty()) {
            // there is no query param, return null
            return new ResponseEntity<>(new ResponseData<>(ResultCode.SUCCESS, PageResult.empty()),
                    HttpStatus.OK);
        }

        validateTransactionAdvanceQueryRequest(request);

        PageResult pageResult = transactionService.advanceQuery(request);

        return new ResponseEntity<>(new ResponseData<>(ResultCode.SUCCESS, pageResult),
                HttpStatus.OK);
    }

    /**
     * Verify interface request parameters.
     * 'from' and 'to' can be used only within seven days.
     * If the value exceeds seven days, 'from' will be reset
     *
     * @param request
     */
    private void validateTransactionAdvanceQueryRequest(TransactionAdvanceQueryRequest request) {
        if (!CountryCode.isValid(request.getCountryCode().name())) {
            throw new BusinessException(ResultCode.INVALID_PARAMETERS.getCode(),
                    "invalid country code: " + request.getCountryCode().name());
        }
        if (!TransactionType.isValid(request.getTransactionType().name())) {
            throw new BusinessException(ResultCode.INVALID_PARAMETERS.getCode(),
                    "invalid transaction type: " + request.getTransactionType().name());
        }
        if (request.getProductCode() != null &&
                !ProductCode.isValid(request.getProductCode().name())) {
            throw new BusinessException(ResultCode.INVALID_PARAMETERS.getCode(),
                    "invalid product code: " + request.getProductCode().name());
        }
        request.setFrom(request.getFrom() * 1000);
        request.setTo(request.getTo() * 1000);
        if (request.getFrom() <= 0 || request.getTo() <= 0) {
            request.setTo(System.currentTimeMillis());
            request.setFrom(request.getTo() - 604800000L);
        }
    }

    /**
     * get uniqueId By after.sub_account_id and after.payment_info.trackingId
     *
     * @param subAccountId
     * @param trackingId
     * @return String uniqueId
     *
     * Interface scrap at 2025-03-27
     *
     */
    @Deprecated
    @GetMapping("/getUniqueId")
    public ResponseEntity<ResponseData<String>> getUniqueIdBySubAccountId(
            @RequestParam("merchantCode") String merchantCode,
            @RequestParam("subAccountId") String subAccountId,
            @RequestParam("trackingId") String trackingId) {

        return new ResponseEntity<>(new ResponseData<String>(ResultCode.SUCCESS, ""),
                HttpStatus.OK);
    }


}
