package com.liquido.aqueducts.service.advance.query;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.liquido.aqueducts.commons.constants.SchemeConsts;
import com.liquido.aqueducts.commons.enums.CountryCode;
import com.liquido.aqueducts.commons.enums.ProductCode;
import com.liquido.aqueducts.commons.enums.TransactionType;
import com.liquido.aqueducts.config.SourceConfig;
import com.liquido.aqueducts.config.source.SourceNode;
import com.liquido.aqueducts.vo.document.AdvanceQueryGroupField;
import com.liquido.aqueducts.vo.request.TransactionAdvanceQueryRequest;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PayoutSpeiAdvanceQueryHandler implements AdvanceQueryHandler {

    private final SourceConfig sourceConfig;
    private final MongoTemplate mongoTemplate;

    @Value("${scheme-config.db.payout}")
    private String dbScheme;

    @Override
    public boolean support(TransactionAdvanceQueryRequest request) {
        return TransactionType.PAY_OUT.equals(request.getTransactionType()) &&
                ProductCode.SPEI.equals(request.getProductCode());
    }

    @Override
    public List<String> executeQuery(TransactionAdvanceQueryRequest request) {
        final SourceNode configNode = sourceConfig.getPayout().get("normal");
        if (configNode == null || configNode.isIgnore()) {
            return Collections.emptyList();
        }

        if (SchemeConsts.PAY_OUT_TRANSACTIONS_DB_SCHEME.equals(dbScheme) &&
                isBeforeTimeLimit(request.getTo(), newPayoutTableTimestamp)) {
            return Collections.emptyList();
        }

        if (SchemeConsts.PAY_OUT_DB_SCHEME.equals(dbScheme) &&
                isAfterTimeLimit(request.getTo(), newPayoutTableTimestamp)) {
            return Collections.emptyList();
        }

        Criteria criteria = null;
        if (SchemeConsts.PAY_OUT_TRANSACTIONS_DB_SCHEME.equals(dbScheme)) {
            criteria = Criteria.where("after.merchant_name").is(request.getMerchantCode())
                    .and("source.ts_ms").gt(request.getFrom()).lte(request.getTo())
                    .and("after.country").is(CountryCode.MX.name())
                    .and("after.payment_type").is("BankTransfer");
        } else if (SchemeConsts.PAY_OUT_DB_SCHEME.equals(dbScheme)) {
            criteria = Criteria.where("source.db").is(configNode.getDb())
                    .and("source.table")
                    .is(configNode.getTablePrefix() + request.getMerchantCode().toLowerCase())
                    .and("source.ts_ms").gt(request.getFrom()).lte(request.getTo())
                    .and("after.country").is(CountryCode.MX.name())
                    .and("after.payment_type").is("BankTransfer");
        }
        if (criteria == null) {
            return Collections.emptyList();
        }
        if (request.getOther().containsKey("accountId")) {
            criteria.and("after.target_account_id").is(request.getOther().get("accountId"));
        }

        final Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group(configNode.getUniqueId())
        );

        return mongoTemplate.aggregate(aggregation,
                        configNode.getCollection(), AdvanceQueryGroupField.class)
                .getMappedResults().stream()
                .map(AdvanceQueryGroupField::get_id)
                .collect(Collectors.toList());
    }
}
