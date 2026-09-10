package com.liquido.aqueducts.service;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.liquido.aqueducts.commons.PageResult;
import com.liquido.aqueducts.commons.constants.MongoDbConsts;
import com.liquido.aqueducts.mapper.ShopifyMapper;
import com.liquido.aqueducts.vo.document.eventlog.ShopifyEventLog;
import com.liquido.aqueducts.vo.response.eventlog.ShopifyOrderItem;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ShopifyService {

    private final MongoTemplate mongoTemplate;

    private final ShopifyMapper shopifyMapper;

    private final JsonMapper jsonMapper;

    @Autowired
    public ShopifyService(MongoTemplate mongoTemplate, ShopifyMapper shopifyMapper, JsonMapper jsonMapper) {
        this.mongoTemplate = mongoTemplate;
        this.shopifyMapper = shopifyMapper;
        this.jsonMapper = jsonMapper;
    }


    public PageResult<ShopifyOrderItem> list(long from, long to) {
        Query query = Query.query(
                Criteria.where("_id")
                        .gt(new ObjectId(Long.toHexString(from) + MongoDbConsts.OBJECT_ID_SUFFIX_ZERO))
                        .lte(new ObjectId(Long.toHexString(to) + MongoDbConsts.OBJECT_ID_SUFFIX_F))
                        .and("after.test").is(false)
        );
        // transfer to ShopifyEventLog.class in mongoTemplate will make "id" fields is null, use Object.class instead.
        List<Object> objectList = mongoTemplate.find(
                query, Object.class, "shopify");
        List<ShopifyEventLog> eventLogs = new ArrayList<>();
        for (Object object : objectList) {
            eventLogs.add(jsonMapper.convertValue(object, ShopifyEventLog.class));
        }
        List<ShopifyOrderItem> shopifyOrderItems = shopifyMapper.toEventLogList(eventLogs);

        return PageResult.<ShopifyOrderItem>builder()
                .totalCount(shopifyOrderItems.size())
                .countLimit(Long.MAX_VALUE)
                .pageSize(Long.MAX_VALUE)
                .page(1L)
                .moreThanLimit(false)
                .results(shopifyOrderItems)
                .build();
    }
}
