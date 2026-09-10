package com.liquido.aqueducts.service;

import com.liquido.aqueducts.commons.PageResult;
import com.liquido.aqueducts.mapper.MetricMapper;
import com.liquido.aqueducts.mapper.TransactionCountMapper;
import com.liquido.aqueducts.vo.document.count.MarketPlaceCountItem;
import com.liquido.aqueducts.vo.document.count.PayinCountItem;
import com.liquido.aqueducts.vo.document.count.PayoutCountItem;
import com.liquido.aqueducts.vo.document.count.SubAccountCountItem;
import com.liquido.aqueducts.vo.document.metric.MarketPlaceMetricItem;
import com.liquido.aqueducts.vo.document.metric.PayinMetricItem;
import com.liquido.aqueducts.vo.document.metric.PayoutMetricItem;
import com.liquido.aqueducts.vo.document.metric.SubAccountMetricItem;
import com.liquido.aqueducts.vo.response.TransactionCountItem;
import com.liquido.aqueducts.vo.response.TransactionMetricItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.Field;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MetricService {

    private final MongoTemplate mongoTemplate;

    private final MetricMapper metricMapper;

    @Autowired
    public MetricService(final MongoTemplate mongoTemplate, final MetricMapper metricMapper) {
        this.metricMapper = metricMapper;
        this.mongoTemplate = mongoTemplate;
    }

    public PageResult<TransactionMetricItem> list(final long from, final long to) {
        List<TransactionMetricItem> results = new ArrayList<>();
        final Field countryField = Fields.field("country", "after.country");
        final Field merchantField = Fields.field("merchant", "source.table");
        // payout
        final Aggregation payoutAggregation = Aggregation.newAggregation(
                Aggregation.match(
                        Criteria.where("source.ts_ms")
                                .gt(from * 1000).lte(to * 1000)
                                .and("op").in("c", "r")
                                .and("after.flags").is(0)
                ),
                Aggregation.group(Fields.from(
                                countryField,
                                Fields.field("merchant", "after.merchant_name"))
                        )
                        .count().as("count")
                        .push("after.amount").as("amountList")
        );
        List<PayoutMetricItem> payoutMetricItems =
                mongoTemplate.aggregate(payoutAggregation,
                                "payout_transactions",
                                PayoutMetricItem.class)
                        .getMappedResults();
        for (PayoutMetricItem item : payoutMetricItems) {
            results.add(metricMapper.payoutToMetricItem(item));
        }

        // payin
        final Aggregation payinAggregation = Aggregation.newAggregation(
                Aggregation.match(
                        Criteria.where("source.ts_ms")
                                .gt(from * 1000).lte(to * 1000)
                                .and("source.db").is("virgo")
                                .and("op").in("c", "r")
                                .and("after.flags").is(0)
                ),
                Aggregation.group(Fields.from(
                                countryField,
                                Fields.field("merchant", "after.merchant_name")
                        ))
                        .count().as("count")
                        .sum("after.amount").as("sumAmount")
        );
        List<PayinMetricItem> payinMetricItems =
                mongoTemplate.aggregate(payinAggregation, "payment", PayinMetricItem.class)
                        .getMappedResults();
        for (PayinMetricItem item : payinMetricItems) {
            results.add(metricMapper.payinToMetricItem(item));
        }

        // marketplace orders
        final Aggregation marketplaceAggregation = Aggregation.newAggregation(
                Aggregation.match(
                        Criteria.where("source.ts_ms")
                                .gt(from * 1000).lte(to * 1000)
                                .and("op").in("c", "r")
                                .and("after.flags").is(0)
                ),
                Aggregation.group(
                                Fields.from(
                                        Fields.field("country", "after.country_code"),
                                        merchantField)
                        )
                        .count().as("count")
                        .push("after.amount").as("amountList")
        );
        List<MarketPlaceMetricItem> marketPlaceMetricItems =
                mongoTemplate.aggregate(marketplaceAggregation, "marketplace",
                                MarketPlaceMetricItem.class)
                        .getMappedResults();
        for (MarketPlaceMetricItem item : marketPlaceMetricItems) {
            results.add(metricMapper.marketplaceToMetricItem(item));
        }

        // subAccount
        final Aggregation subAccountAggregation = Aggregation.newAggregation(
                Aggregation.match(
                        Criteria.where("source.ts_ms")
                                .gt(from * 1000).lte(to * 1000)
                                .and("source.db").is("liquido")
                                .and("op").in("c", "r")
                                .and("after.flags").is(0)
                ),
                Aggregation.group("source.table")
                        .count().as("count")
                        .push("after.amount").as("amountList")
        );
        List<SubAccountMetricItem> subAccountMetricItems =
                mongoTemplate.aggregate(subAccountAggregation, "payment",
                                SubAccountMetricItem.class)
                        .getMappedResults();
        for (SubAccountMetricItem item : subAccountMetricItems) {
            results.add(metricMapper.subAccountToMetricItem(item));
        }

        results = results.stream().collect(Collectors.groupingBy(
                        v -> TransactionMetricItem.builder()
                                .merchantCode(v.getMerchantCode()).countryCode(v.getCountryCode())
                                .transactionType(v.getTransactionType()).currency(v.getCurrency())
                                .build()))
                .entrySet().stream().map(v -> {
                    TransactionMetricItem item = v.getKey();
                    item.setSumAmount(
                            v.getValue().stream().mapToLong(TransactionMetricItem::getSumAmount)
                                    .sum());
                    item.setCountTransaction(v.getValue().stream()
                            .mapToLong(TransactionMetricItem::getCountTransaction).sum());
                    return item;
                }).collect(Collectors.toList());

        return PageResult.<TransactionMetricItem>builder()
                .totalCount(results.size())
                .countLimit(Long.MAX_VALUE)
                .pageSize(Long.MAX_VALUE)
                .page(1L)
                .moreThanLimit(false)
                .results(results)
                .build();
    }

    public PageResult<TransactionCountItem> rate(long from, long to) {
        List<TransactionCountItem> results = new ArrayList<>();
        Field countryField = Fields.field("country", "after.country");
        Field merchantField = Fields.field("merchant", "source.table");

        // payin
        Aggregation payinTotalAggregation = Aggregation.newAggregation(
                Aggregation.match(
                        Criteria.where("source.ts_ms")
                                .gt(from * 1000).lte(to * 1000)
                                .and("source.db").is("virgo")
                                .and("op").in("c", "r")
                                .and("after.flags").is(0)
                ),
                Aggregation.group(Fields.from(countryField,
                                Fields.field("merchant", "after.merchant_name"),
                                Fields.field("product", "after.payment_method")))
                        .count().as("count")
        );
        List<PayinCountItem> payinTotalItems =
                mongoTemplate.aggregate(payinTotalAggregation, "payment", PayinCountItem.class)
                        .getMappedResults();
        Aggregation payinSuccessAggregation = Aggregation.newAggregation(
                Aggregation.match(
                        Criteria.where("source.ts_ms")
                                .gt(from * 1000).lte(to * 1000)
                                .and("source.db").is("virgo")
                                .and("op").in("c", "u", "r")
                                .and("after.flags").is(0)
                                .and("after.transfer_status").is("SETTLED")
                ),
                Aggregation.group(Fields.from(countryField,
                                Fields.field("merchant", "after.merchant_name"),
                                Fields.field("product", "after.payment_method")))
                        .count().as("count")
        );
        List<PayinCountItem> payinSuccessItems =
                mongoTemplate.aggregate(payinSuccessAggregation, "payment", PayinCountItem.class)
                        .getMappedResults();
        results.addAll(TransactionCountMapper.payinListToItem(payinTotalItems, payinSuccessItems));

        // payout
        Aggregation payoutTotalAggregation = Aggregation.newAggregation(
                Aggregation.match(
                        Criteria.where("source.ts_ms")
                                .gt(from * 1000).lte(to * 1000)
                                .and("op").in("c", "r")
                                .and("after.flags").is(0)
                ),
                Aggregation.group(Fields.from(
                                countryField,
                                Fields.field("merchant", "after.merchant_name"),
                                Fields.field("product", "after.payment_type")))
                        .count().as("count")
        );
        List<PayoutCountItem> payoutTotalItems =
                mongoTemplate.aggregate(payoutTotalAggregation,
                                "payout_transactions",
                                PayoutCountItem.class)
                        .getMappedResults();
        Aggregation payoutSuccessAggregation = Aggregation.newAggregation(
                Aggregation.match(
                        Criteria.where("source.ts_ms")
                                .gt(from * 1000).lte(to * 1000)
                                .and("op").in("c", "u", "r")
                                .and("after.flags").is(0)
                                .and("after.status").is("SETTLED")
                ),
                Aggregation.group(Fields.from(
                                countryField,
                                Fields.field("merchant", "after.merchant_name"),
                                Fields.field("product", "after.payment_type")))
                        .count().as("count")
        );
        List<PayoutCountItem> payoutSuccessItems =
                mongoTemplate.aggregate(payoutSuccessAggregation,
                                "payout_transactions",
                                PayoutCountItem.class)
                        .getMappedResults();
        results.addAll(
                TransactionCountMapper.payoutListToItem(payoutTotalItems, payoutSuccessItems));

        // marketplace
        Aggregation marketplaceTotalAggregation = Aggregation.newAggregation(
                Aggregation.match(
                        Criteria.where("source.ts_ms")
                                .gt(from * 1000).lte(to * 1000)
                                .and("op").in("c", "r")
                                .and("after.flags").is(0)
                ),
                Aggregation.group(
                        Fields.from(
                                Fields.field("country", "after.country_code"),
                                merchantField,
                                Fields.field("product", "after.category"))
                ).count().as("count")
        );
        List<MarketPlaceCountItem> marketplaceTotalItems =
                mongoTemplate.aggregate(marketplaceTotalAggregation, "marketplace",
                                MarketPlaceCountItem.class)
                        .getMappedResults();
        Aggregation marketplaceSuccessAggregation = Aggregation.newAggregation(
                Aggregation.match(
                        Criteria.where("source.ts_ms")
                                .gt(from * 1000).lte(to * 1000)
                                .and("op").in("c", "u", "r")
                                .and("after.flags").is(0)
                                .and("after.order_status").is("SETTLED")
                ),
                Aggregation.group(
                        Fields.from(
                                Fields.field("country", "after.country_code"),
                                merchantField,
                                Fields.field("product", "after.category"))
                ).count().as("count")
        );
        List<MarketPlaceCountItem> marketplaceSuccessItems =
                mongoTemplate.aggregate(marketplaceSuccessAggregation, "marketplace",
                                MarketPlaceCountItem.class)
                        .getMappedResults();
        results.addAll(TransactionCountMapper.marketplaceListToItem(marketplaceTotalItems,
                marketplaceSuccessItems));

        // subAccount
        Aggregation subAccountTotalAgg = Aggregation.newAggregation(
                Aggregation.match(
                        Criteria.where("source.ts_ms")
                                .gt(from * 1000).lte(to * 1000)
                                .and("source.db").is("liquido")
                                .and("op").in("c", "r")
                                .and("after.flags").is(0)
                ),
                Aggregation.group(Fields.from(merchantField)).count().as("count")
        );
        List<SubAccountCountItem> subAccountTotalItems =
                mongoTemplate.aggregate(subAccountTotalAgg, "payment", SubAccountCountItem.class)
                        .getMappedResults();
        Aggregation subAccountSuccessAgg = Aggregation.newAggregation(
                Aggregation.match(
                        Criteria.where("source.ts_ms")
                                .gt(from * 1000).lte(to * 1000)
                                .and("source.db").is("liquido")
                                .and("op").in("u", "r", "c")
                                .and("after.flags").is(0)
                                .and("after.status").is("SETTLED")
                ),
                Aggregation.group(Fields.from(merchantField)).count().as("count")
        );
        List<SubAccountCountItem> subAccountSuccessItems =
                mongoTemplate.aggregate(subAccountSuccessAgg, "payment", SubAccountCountItem.class)
                        .getMappedResults();
        results.addAll(TransactionCountMapper.SubAccountListToItem(subAccountTotalItems,
                subAccountSuccessItems));

        return PageResult.<TransactionCountItem>builder()
                .totalCount(results.size())
                .countLimit(Long.MAX_VALUE)
                .pageSize(Long.MAX_VALUE)
                .page(1L)
                .moreThanLimit(false)
                .results(results)
                .build();
    }

}
