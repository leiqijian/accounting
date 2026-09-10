package com.liquido.aqueducts.service;

import com.liquido.aqueducts.commons.PageResult;
import com.liquido.aqueducts.commons.constants.MongoDbConsts;
import com.liquido.aqueducts.mapper.PaymentLinkMapper;
import com.liquido.aqueducts.vo.document.eventlog.PaymentLinkEventLog;
import com.liquido.aqueducts.vo.response.eventlog.PaymentLinkItem;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class PaymentLinkService {

    private final MongoTemplate mongoTemplate;

    private final PaymentLinkMapper paymentLinkMapper;

    @Autowired
    public PaymentLinkService(MongoTemplate mongoTemplate, PaymentLinkMapper paymentLinkMapper) {
        this.mongoTemplate = mongoTemplate;
        this.paymentLinkMapper = paymentLinkMapper;
    }

    public PageResult<PaymentLinkItem> list(long from, long to) {
        Query query = Query.query(
                Criteria.where("_id")
                    .gt(new ObjectId(Long.toHexString(from) + MongoDbConsts.OBJECT_ID_SUFFIX_ZERO))
                    .lte(new ObjectId(Long.toHexString(to) + MongoDbConsts.OBJECT_ID_SUFFIX_F))
                        .and("op").ne("d")
        );
        List<PaymentLinkEventLog> eventLogs = mongoTemplate.find(
                query, PaymentLinkEventLog.class, "payment_link");
        List<PaymentLinkItem> paymentLinkItems = paymentLinkMapper.toEventLogList(eventLogs);

        return PageResult.<PaymentLinkItem>builder()
                .totalCount(paymentLinkItems.size())
                .countLimit(Long.MAX_VALUE)
                .pageSize(Long.MAX_VALUE)
                .page(1L)
                .moreThanLimit(false)
                .results(paymentLinkItems)
                .build();
    }
}
