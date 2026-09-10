package com.liquido.aqueducts.controller;


import com.liquido.aqueducts.commons.PageResult;
import com.liquido.aqueducts.commons.ResponseData;
import com.liquido.aqueducts.commons.enums.ResultCode;
import com.liquido.aqueducts.service.PaymentLinkService;
import com.liquido.aqueducts.service.ShopifyService;
import com.liquido.aqueducts.service.ShoplazzaService;
import com.liquido.aqueducts.service.TokenService;
import com.liquido.aqueducts.service.TransactionEventLogService;
import com.liquido.aqueducts.vo.response.eventlog.PaymentLinkItem;
import com.liquido.aqueducts.vo.response.eventlog.ShopifyOrderItem;
import com.liquido.aqueducts.vo.response.eventlog.ShoplazzaOrderItem;
import com.liquido.aqueducts.vo.response.eventlog.TokenVaultItem;
import com.liquido.aqueducts.vo.response.eventlog.TransactionEventLogItem;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/event_log")
@Slf4j
@AllArgsConstructor
public class TransactionEventLogController {

    private final TransactionEventLogService transactionEventLogService;

    private final PaymentLinkService paymentLinkService;

    private final ShopifyService shopifyService;

    private final ShoplazzaService shoplazzaService;

    private final TokenService tokenService;

    @GetMapping("/")
    public ResponseEntity<ResponseData<PageResult<TransactionEventLogItem>>> list(
            @RequestParam(value = "from") final long from,
            @RequestParam(value = "to") final long to
    ) {
        PageResult<TransactionEventLogItem> page = transactionEventLogService.list(from, to);
        return new ResponseEntity<>(new ResponseData<>(ResultCode.SUCCESS, page), HttpStatus.OK);
    }

    /**
     * Add an interface similar to event_log.
     * Unlike event_log, the time range of the query is based on the timestamp of ObjectId.
     *
     * @param from
     * @param to
     * @return
     */
    @GetMapping("/list")
    public ResponseEntity<ResponseData<PageResult<TransactionEventLogItem>>> findByObjectIdList(
            @RequestParam(value = "from") final long from,
            @RequestParam(value = "to") final long to
    ) {
        PageResult<TransactionEventLogItem> page =
                transactionEventLogService.findByObjectIdList(from, to);
        return new ResponseEntity<>(new ResponseData<>(ResultCode.SUCCESS, page), HttpStatus.OK);
    }


    @GetMapping("/payment_link")
    public ResponseEntity<ResponseData<PageResult<PaymentLinkItem>>> paymentLink(
            @RequestParam(value = "from") final long from,
            @RequestParam(value = "to") final long to
    ) {
        PageResult<PaymentLinkItem> page = paymentLinkService.list(from, to);
        return new ResponseEntity<>(new ResponseData<>(ResultCode.SUCCESS, page), HttpStatus.OK);
    }

    @GetMapping("/shopify")
    public ResponseEntity<ResponseData<PageResult<ShopifyOrderItem>>> shopify(
            @RequestParam(value = "from") final long from,
            @RequestParam(value = "to") final long to
    ) {
        PageResult<ShopifyOrderItem> page = shopifyService.list(from, to);
        return new ResponseEntity<>(new ResponseData<>(ResultCode.SUCCESS, page), HttpStatus.OK);
    }

    @GetMapping("/shoplazza")
    public ResponseEntity<ResponseData<PageResult<ShoplazzaOrderItem>>> shoplazza(
            @RequestParam(value = "from") final long from,
            @RequestParam(value = "to") final long to
    ) {
        PageResult<ShoplazzaOrderItem> page = shoplazzaService.list(from, to);
        return new ResponseEntity<>(new ResponseData<>(ResultCode.SUCCESS, page), HttpStatus.OK);
    }

    @GetMapping("/token")
    public ResponseEntity<ResponseData<PageResult<TokenVaultItem>>> token(
            @RequestParam(value = "from") final long from,
            @RequestParam(value = "to") final long to
    ) {
        return new ResponseEntity<>(
                new ResponseData<>(ResultCode.SUCCESS, tokenService.list(from, to)), HttpStatus.OK);
    }


}
