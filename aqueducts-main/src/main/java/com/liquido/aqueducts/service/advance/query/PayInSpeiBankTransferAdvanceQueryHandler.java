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
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PayInSpeiBankTransferAdvanceQueryHandler implements AdvanceQueryHandler{

    private final SourceConfig sourceConfig;
    private final MongoTemplate mongoTemplate;

    @Override
    public boolean support(TransactionAdvanceQueryRequest request) {
        return TransactionType.PAY_IN.equals(request.getTransactionType()) &&
                ProductCode.SPEI_BANK_TRANSFER.equals(request.getProductCode());
    }

    @Override
    public List<String> executeQuery(TransactionAdvanceQueryRequest request) {

        return isAfterTimeLimit(request.getTo(), newPayInTableTimestamp) ?
                queryPayinNormalData(request) : queryPayinBackupData(request);
    }

    /**
     * query mongo payment table data
     * @param request
     * @return
     */
    private List<String> queryPayinNormalData(TransactionAdvanceQueryRequest request) {
        final SourceNode normalConfig = sourceConfig.getPayin().get("normal");

        if (normalConfig != null && !normalConfig.isIgnore()) {
            Criteria criteria =
                    Criteria.where("after.merchant_name")
                            .is(request.getMerchantCode().toLowerCase())
                            .and("source.ts_ms").gt(request.getFrom()).lte(request.getTo())
                            .and("after.country").is(CountryCode.MX.name())
                            .and("after.payment_method").is(PaymentMethodCode.BANK_TRANSFER.name());
            if (request.getOther().containsKey("accountId")) {
                criteria.and("after.payment_info")
                        .regex(request.getOther().get("accountId"));
            }
            if (request.getOther().containsKey("email")) {
                criteria.and("after.payer").regex(request.getOther().get("email"));
            }

            final Aggregation aggregation = Aggregation.newAggregation(
                    Aggregation.match(criteria),
                    Aggregation.group(normalConfig.getUniqueId())
            );
            return mongoTemplate.aggregate(aggregation,
                            normalConfig.getCollection(), AdvanceQueryGroupField.class)
                    .getMappedResults().stream()
                    .map(AdvanceQueryGroupField::get_id)
                    .collect(Collectors.toList());

        }

        return Collections.emptyList();
    }

    /**
     * query mongo payin table data
     * @param request
     * @return
     */
    private List<String> queryPayinBackupData(TransactionAdvanceQueryRequest request) {
        final SourceNode backupConfig = sourceConfig.getPayin().get("backup");

        if (backupConfig != null && !backupConfig.isIgnore()) {
            Criteria criteria =
                    Criteria.where("source.db").is(backupConfig.getDb())
                            .and("source.table").is(backupConfig.getTablePrefix() +
                                    request.getMerchantCode().toLowerCase())
                            .and("source.ts_ms").gt(request.getFrom()).lte(request.getTo())
                            .and("after.country").is(CountryCode.MX.name())
                            .and("after.payment_method").is(PaymentMethodCode.BANK_TRANSFER.name());
            if (request.getOther().containsKey("accountId")) {
                criteria.and("after.payment_info")
                        .regex(request.getOther().get("accountId"));
            }
            if (request.getOther().containsKey("email")) {
                criteria.and("after.payer").regex(request.getOther().get("email"));
            }

            final Aggregation aggregation = Aggregation.newAggregation(
                    Aggregation.match(criteria),
                    Aggregation.group(backupConfig.getUniqueId())
            );
            return mongoTemplate.aggregate(aggregation,
                            backupConfig.getCollection(), AdvanceQueryGroupField.class)
                    .getMappedResults().stream()
                    .map(AdvanceQueryGroupField::get_id)
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }
}
