package com.liquido.aqueducts.service.advance.query;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.liquido.aqueducts.commons.enums.CountryCode;
import com.liquido.aqueducts.commons.enums.PaymentMethodCode;
import com.liquido.aqueducts.commons.enums.ProductCode;
import com.liquido.aqueducts.commons.enums.TransactionType;
import com.liquido.aqueducts.config.SourceConfig;
import com.liquido.aqueducts.config.source.SourceNode;
import com.liquido.aqueducts.vo.document.AdvanceQueryGroupField;
import com.liquido.aqueducts.vo.request.TransactionAdvanceQueryRequest;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PayInSpeiVaAdvanceQueryHandler implements AdvanceQueryHandler {

    private final SourceConfig sourceConfig;
    private final MongoTemplate mongoTemplate;

    @Override
    public boolean support(TransactionAdvanceQueryRequest request) {
        return TransactionType.PAY_IN.equals(request.getTransactionType()) &&
                ProductCode.SPEI_VA.equals(request.getProductCode());
    }

    @Override
    public List<String> executeQuery(TransactionAdvanceQueryRequest request) {

        // query mongo payment table about sub account data
        final List<String> list1 = isAfterTimeLimit(request.getTo(), newPayInTableTimestamp) ?
                querySubAccountNormalData(request) : Collections.emptyList();

        // query mongo payin table about sub account data
        final List<String> list2 = isBeforeTimeLimit(request.getTo(), newPayInTableTimestamp) ?
                querySubAccountBackupData(request) : Collections.emptyList();

        // query mongo payment table about virgo data
        final List<String> list3 = isAfterTimeLimit(request.getTo(), newPayInTableTimestamp) ?
                queryPayInNormalData(request) : Collections.emptyList();

        return Stream.of(list1, list2, list3)
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    private List<String> querySubAccountNormalData(TransactionAdvanceQueryRequest request) {
        final SourceNode subAccountNormalConfig = sourceConfig.getSubAccount().get("normal");

        if (subAccountNormalConfig != null && !subAccountNormalConfig.isIgnore()) {
            Criteria criteria = Criteria.where("source.db").is(subAccountNormalConfig.getDb())
                    .and("source.table").is(subAccountNormalConfig.getTablePrefix() +
                            request.getMerchantCode().toLowerCase())
                    .and("source.ts_ms").gt(request.getFrom()).lte(request.getTo())
                    .and("after.description").is("payback auto transfer");

            if (request.getOther().containsKey("accountId")) {
                criteria.and("after.sub_account_id").is(request.getOther().get("accountId"));
            }
            if (request.getOther().containsKey("email")) {
                criteria.and("after.payer").regex(request.getOther().get("email"));
            }

            Criteria trackingIdCriteria = null;
            if (request.getOther().containsKey("trackingId")) {
                trackingIdCriteria = Criteria.where("after.payment_info")
                        .regex(request.getOther().get("trackingId"));
            }

            final Criteria finalCriteria = Objects.isNull(trackingIdCriteria) ? criteria :
                    new Criteria().andOperator(criteria, trackingIdCriteria);

            final Aggregation aggregation = Aggregation.newAggregation(
                    Aggregation.match(finalCriteria),
                    Aggregation.group(subAccountNormalConfig.getUniqueId())
            );

            return mongoTemplate.aggregate(aggregation,
                            subAccountNormalConfig.getCollection(), AdvanceQueryGroupField.class)
                    .getMappedResults().stream()
                    .map(AdvanceQueryGroupField::get_id)
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    private List<String> querySubAccountBackupData(TransactionAdvanceQueryRequest request) {
        final SourceNode subAccountBackupConfig = sourceConfig.getSubAccount().get("backup");

        if (subAccountBackupConfig != null && !subAccountBackupConfig.isIgnore()) {
            Criteria criteria = Criteria.where("source.db").is(subAccountBackupConfig.getDb())
                    .and("source.table").is(subAccountBackupConfig.getTablePrefix() +
                            request.getMerchantCode().toLowerCase())
                    .and("source.ts_ms").gt(request.getFrom()).lte(request.getTo())
                    .and("after.description").is("payback auto transfer");
            if (request.getOther().containsKey("accountId")) {
                criteria.and("after.sub_account_id").is(request.getOther().get("accountId"));
            }
            if (request.getOther().containsKey("email")) {
                criteria.and("after.payer").regex(request.getOther().get("email"));
            }
            Criteria trackingIdCriteria = null;
            if (request.getOther().containsKey("trackingId")) {
                trackingIdCriteria = Criteria.where("after.payment_info")
                        .regex(request.getOther().get("trackingId"));
            }
            final Criteria finalCriteria = Objects.isNull(trackingIdCriteria) ? criteria :
                    new Criteria().andOperator(criteria, trackingIdCriteria);

            Aggregation aggregation = Aggregation.newAggregation(
                    Aggregation.match(finalCriteria),
                    Aggregation.group(subAccountBackupConfig.getUniqueId())
            );

            return mongoTemplate.aggregate(aggregation,
                            subAccountBackupConfig.getCollection(), AdvanceQueryGroupField.class)
                    .getMappedResults().stream()
                    .map(AdvanceQueryGroupField::get_id)
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    private List<String> queryPayInNormalData(TransactionAdvanceQueryRequest request) {
        final SourceNode payInNormalConfig = sourceConfig.getPayin().get("normal");

        if (payInNormalConfig != null && !payInNormalConfig.isIgnore()) {
            Criteria criteria = Criteria.where("after.merchant_name")
                    .is(request.getMerchantCode().toLowerCase())
                    .and("source.ts_ms").gt(request.getFrom()).lte(request.getTo())
                    .and("after.country").is(CountryCode.MX.name())
                    .and("after.payment_method").is(PaymentMethodCode.CLABE_ACCOUNT.name());
            if (request.getOther().containsKey("accountId")) {
                criteria.and("after.payment_info").regex(request.getOther().get("accountId"));
            }
            if (request.getOther().containsKey("email")) {
                criteria.and("after.payer").regex(request.getOther().get("email"));
            }
            Criteria trackingIdCriteria = null;
            if (request.getOther().containsKey("trackingId")) {
                trackingIdCriteria = Criteria.where("after.payment_info")
                        .regex(request.getOther().get("trackingId"));
            }
            final Criteria finalCriteria = Objects.isNull(trackingIdCriteria) ? criteria :
                    new Criteria().andOperator(criteria, trackingIdCriteria);

            Aggregation aggregation = Aggregation.newAggregation(
                    Aggregation.match(finalCriteria),
                    Aggregation.group(payInNormalConfig.getUniqueId())
            );

            return mongoTemplate.aggregate(aggregation,
                            payInNormalConfig.getCollection(), AdvanceQueryGroupField.class)
                    .getMappedResults().stream()
                    .map(AdvanceQueryGroupField::get_id)
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }
}
