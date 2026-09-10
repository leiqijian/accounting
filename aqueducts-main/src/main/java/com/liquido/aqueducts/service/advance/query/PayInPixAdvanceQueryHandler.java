package com.liquido.aqueducts.service.advance.query;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
public class PayInPixAdvanceQueryHandler implements AdvanceQueryHandler {

    private final SourceConfig sourceConfig;
    private final MongoTemplate mongoTemplate;

    @Override
    public boolean support(TransactionAdvanceQueryRequest request) {
        return TransactionType.PAY_IN.equals(request.getTransactionType()) &&
                ProductCode.PIX.equals(request.getProductCode());
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
            final Criteria criteria = Criteria.where("after.merchant_name")
                    .is(request.getMerchantCode().toLowerCase())
                    .and("source.ts_ms").gt(request.getFrom()).lte(request.getTo())
                    .and(normalConfig.getProductType())
                    .in(PaymentMethodCode.PIX_STATIC_QR.name(),
                            PaymentMethodCode.PIX_DYNAMIC_QR.name());

            addPixQueryCriteria(criteria, request.getOther());

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
                            .and(backupConfig.getProductType())
                            .in(PaymentMethodCode.PIX_STATIC_QR.name(),
                                    PaymentMethodCode.PIX_DYNAMIC_QR.name());

            addPixQueryCriteria(criteria, request.getOther());

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

    private void addPixQueryCriteria(Criteria criteria, Map<String, String> other) {
        if (other.containsKey("e2nid")) {
            criteria.and("after.payment_info").regex(other.get("e2nid"));
        }
        if (other.containsKey("pixKey")) {
            criteria.and("after.payer").regex(other.get("pixKey"));
        }
        if (other.containsKey("cpf") || other.containsKey("cnpj")) {
            String str = other.get("cpf") == null ? other.get("cnpj") : other.get("cpf");
            criteria.and("after.payer").regex(str);
        }
        if (other.containsKey("email")) {
            criteria.and("after.payer").regex(other.get("email"));
        }
    }
}
