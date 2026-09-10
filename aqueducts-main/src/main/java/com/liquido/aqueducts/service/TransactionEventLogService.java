package com.liquido.aqueducts.service;


import com.liquido.aqueducts.commons.PageResult;
import com.liquido.aqueducts.commons.constants.MongoDbConsts;
import com.liquido.aqueducts.commons.constants.SchemeConsts;
import com.liquido.aqueducts.config.SourceConfig;
import com.liquido.aqueducts.mapper.EventLogListMapper;
import com.liquido.aqueducts.vo.document.eventlog.*;
import com.liquido.aqueducts.vo.response.eventlog.TransactionEventLogItem;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TransactionEventLogService {

    private final MongoTemplate mongoTemplate;

    private final EventLogListMapper eventLogListMapper;

    private final SourceConfig sourceConfig;

    @Value("${scheme-config.db.payout}")
    private String dbScheme;

    @Autowired
    public TransactionEventLogService(
            final MongoTemplate mongoTemplate,
            final EventLogListMapper eventLogListMapper,
            final SourceConfig sourceConfig) {
        this.mongoTemplate = mongoTemplate;
        this.eventLogListMapper = eventLogListMapper;
        this.sourceConfig = sourceConfig;
    }

    public PageResult<TransactionEventLogItem> list(final long from, final long to) {

        // fetch the data of each transaction type
        // payout
        Query queryPayout = Query.query(
                Criteria.where("source.ts_ms").gt(from * 1000).lte(to * 1000)
                        .and("after.flags").is(0)
                        .and("source.table").ne("payout_liquido_cn")  // hard code, liquido is not a merchant.
        );
        List<TransactionEventLogItem> results = new ArrayList<>(getPayoutEventLog(queryPayout, 0));

        // payin
        Query queryPayin = Query.query(
                Criteria.where("source.ts_ms").gt(from * 1000).lte(to * 1000)
                        .and("source.db").is("virgo")
                        .and("after.flags").is(0)
                        .and("op").ne("d")
        );
        List<PayinEventLog> payinEventLogs =
                mongoTemplate.find(queryPayin, PayinEventLog.class, "payment");
        results.addAll(
                payinEventLogs.stream().map(eventLog -> {
                    TransactionEventLogItem item = eventLogListMapper.toEventLog(eventLog);
                    item.setEventTime(eventLog.getSource().getTs_ms() / 1000);
                    return item;
                }).collect(Collectors.toList())
        );

        // marketplace orders
        Query queryMarketplace = Query.query(
                Criteria.where("source.ts_ms").gt(from * 1000).lte(to * 1000)
                        .and("after.flags").is(0)
        );
        List<MarketPlaceOrderEventLog> orderEventLogs = mongoTemplate.find(
                queryMarketplace, MarketPlaceOrderEventLog.class, "marketplace");
        results.addAll(
                orderEventLogs.stream().map(eventLog -> {
                    TransactionEventLogItem item = eventLogListMapper.toEventLog(eventLog);
                    item.setEventTime(eventLog.getSource().getTs_ms() / 1000);
                    return item;
                }).collect(Collectors.toList())
        );

        return PageResult.<TransactionEventLogItem>builder()
                .totalCount(results.size())
                .countLimit(Long.MAX_VALUE)
                .pageSize(Long.MAX_VALUE)
                .page(1L)
                .moreThanLimit(false)
                .results(results)
                .build();
    }

    private List<SubAccountPayBackEventLog> getSubAccounts(final long from, final long to) {
        Criteria criteria = Criteria.where("source.ts_ms").gt(from * 1000).lte(to * 1000)
                .and("source.db").is("liquido")
                .and("after.flags").is(0);
        Aggregation subAccountsAggregation = Aggregation.newAggregation(
                Aggregation.match(criteria)
        );
        subAccountsAggregation.getPipeline().add(
                Aggregation.lookup("sub_account", "after.sub_account_id",
                        "after.account_id", "after.sub_account_info")
        );
        final AggregationResults<SubAccountPayBackEventLog> aggregateResults =
                mongoTemplate.aggregate(
                        subAccountsAggregation, "payment", SubAccountPayBackEventLog.class);
        return aggregateResults.getMappedResults();
    }

    /**
     * We convert the second timestamp to hexadecimal and supplement the remaining 16 bits with 0 or f.
     * Then use it as the query scope of ObjectId.
     *
     * @param from
     * @param to
     * @return
     */
    public PageResult<TransactionEventLogItem> findByObjectIdList(final long from, final long to) {

        StopWatch watch = new StopWatch();
        // fetch the data of each transaction type
        // payout
        watch.start("payout event log");
        final var fromOffset = new ObjectId(
                Long.toHexString(from) + MongoDbConsts.OBJECT_ID_SUFFIX_ZERO);
        final var toOffset =
                new ObjectId(Long.toHexString(to) + MongoDbConsts.OBJECT_ID_SUFFIX_F);
        Query queryPayout = Query.query(
                Criteria.where("_id")
                        .gt(fromOffset)
                        .lte(toOffset)
                        .and("after.flags").is(0)
        );
        List<TransactionEventLogItem> results = new ArrayList<>(getPayoutEventLog(queryPayout, 1));
        watch.stop();

        // payin
        watch.start("payin event log");
        Query queryPayin = Query.query(
                Criteria.where("_id")
                        .gt(fromOffset)
                        .lte(toOffset)
                        .and("source.db").is("virgo")
                        .and("after.flags").is(0)
                        .and("op").ne("d")
        );
        List<PayinEventLog> payinEventLogs =
                sourceConfig.getPayin().get("normal").isIgnore() ? new ArrayList<>() :
                        mongoTemplate.find(queryPayin, PayinEventLog.class, "payment");
        results.addAll(
                payinEventLogs.stream().map(eventLog -> {
                    TransactionEventLogItem item = eventLogListMapper.toEventLog(eventLog);
                    item.setEventTime(((ObjectId) eventLog.get_id()).getTimestamp());
                    return item;
                }).collect(Collectors.toList())
        );
        watch.stop();

        // marketplace orders
        watch.start("market place event log");
        Query queryMarketplace = Query.query(
                Criteria.where("_id")
                        .gt(fromOffset)
                        .lte(toOffset)
                        .and("after.flags").is(0)
        );
        List<MarketPlaceOrderEventLog> orderEventLogs =
                sourceConfig.getMarketplace().get("normal").isIgnore() ? new ArrayList<>() :
                        mongoTemplate.find(
                                queryMarketplace, MarketPlaceOrderEventLog.class, "marketplace");
        results.addAll(
                orderEventLogs.stream().map(eventLog -> {
                    TransactionEventLogItem item = eventLogListMapper.toEventLog(eventLog);
                    item.setEventTime(((ObjectId) eventLog.get_id()).getTimestamp());
                    return item;
                }).collect(Collectors.toList())
        );
        watch.stop();

        log.info("event log: {}", watch.prettyPrint());
        log.info("EventLog interface execution time: {} second", watch.getTotalTimeSeconds());

        return PageResult.<TransactionEventLogItem>builder()
                .totalCount(results.size())
                .countLimit(Long.MAX_VALUE)
                .pageSize(Long.MAX_VALUE)
                .page(1L)
                .moreThanLimit(false)
                .results(results)
                .build();
    }

    /**
     * Similar to the findByObjectIdList method, we customize the ObjectId object
     *
     * @param from
     * @param to
     * @return
     */
    private List<SubAccountPayBackEventLog> getSubAccountsByObjectId(final long from, final long to) {
        Criteria criteria = Criteria.where("_id")
                .gt(new ObjectId(Long.toHexString(from) + MongoDbConsts.OBJECT_ID_SUFFIX_ZERO))
                .lte(new ObjectId(Long.toHexString(to) + MongoDbConsts.OBJECT_ID_SUFFIX_F))
                .and("source.db").is("liquido")
                .and("after.flags").is(0);
        Aggregation subAccountsAggregation = Aggregation.newAggregation(
                Aggregation.match(criteria)
        );
        subAccountsAggregation.getPipeline().add(
                Aggregation.lookup("sub_account", "after.sub_account_id",
                        "after.account_id", "after.sub_account_info")
        );
        final AggregationResults<SubAccountPayBackEventLog> aggregateResults =
                mongoTemplate.aggregate(
                        subAccountsAggregation, "payment", SubAccountPayBackEventLog.class);
        return aggregateResults.getMappedResults();
    }

    private List<TransactionEventLogItem> getPayoutEventLog(Query query, int flag) {
        List<TransactionEventLogItem> res = new ArrayList<>();
        if (SchemeConsts.PAY_OUT_TRANSACTIONS_DB_SCHEME.equals(dbScheme)) {
            List<PayoutEventLog> payoutEventLogs =
                    sourceConfig.getPayout().get("normal").isIgnore() ? new ArrayList<>() :
                            mongoTemplate.find(query, PayoutEventLog.class, "payout_transactions");
            res.addAll(
                    payoutEventLogs.stream().map(eventLog -> {
                        TransactionEventLogItem item = eventLogListMapper.toEventLog(eventLog);
                        item.setEventTime(flag == 0 ?
                                eventLog.getSource().getTs_ms() / 1000 :
                                ((ObjectId) eventLog.get_id()).getTimestamp());
                        return item;
                    }).collect(Collectors.toList())
            );
            return res;
        }

        List<PayoutBackEventLog> eventLogs =
                sourceConfig.getPayout().get("normal").isIgnore() ? new ArrayList<>() :
                        mongoTemplate.find(query, PayoutBackEventLog.class, "payout");
        res.addAll(
                eventLogs.stream().map(eventLog -> {
                    TransactionEventLogItem item = eventLogListMapper.toEventLog(eventLog);
                    item.setEventTime(flag == 0 ?
                            eventLog.getSource().getTs_ms() / 1000 :
                            ((ObjectId) eventLog.get_id()).getTimestamp());
                    return item;
                }).collect(Collectors.toList())
        );
        return res;
    }
}
