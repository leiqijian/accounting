package com.liquido.aqueducts.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.liquido.aqueducts.commons.PageResult;
import com.liquido.aqueducts.commons.constants.SchemeConsts;
import com.liquido.aqueducts.config.SourceConfig;
import com.liquido.aqueducts.config.source.SourceNode;
import com.liquido.aqueducts.mapper.TransactionDetailsMapper;
import com.liquido.aqueducts.service.advance.query.AdvanceQueryHandler;
import com.liquido.aqueducts.vo.document.eventlog.MarketPlaceOrderEventLog;
import com.liquido.aqueducts.vo.document.eventlog.PayinEventLog;
import com.liquido.aqueducts.vo.document.eventlog.PayoutBackEventLog;
import com.liquido.aqueducts.vo.document.eventlog.PayoutEventLog;
import com.liquido.aqueducts.vo.document.eventlog.SubAccountEventLog;
import com.liquido.aqueducts.vo.document.eventlog.SubAccountPayBackEventLog;
import com.liquido.aqueducts.vo.request.TransactionAdvanceQueryRequest;
import com.liquido.aqueducts.vo.request.TransactionDetailRequest;
import com.liquido.aqueducts.vo.response.BankTransferDetail;
import com.liquido.aqueducts.vo.response.MarketPlaceDetail;
import com.liquido.aqueducts.vo.response.PayCashDetail;
import com.liquido.aqueducts.vo.response.PayinBankTransferDetail;
import com.liquido.aqueducts.vo.response.PayinCardDetail;
import com.liquido.aqueducts.vo.response.PayinDefaultDetail;
import com.liquido.aqueducts.vo.response.PayinPixDetail;
import com.liquido.aqueducts.vo.response.PayinSpeiVaDetail;
import com.liquido.aqueducts.vo.response.PayoutDetail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionService {

    private final SourceConfig sourceConfig;
    private final MongoTemplate mongoTemplate;
    private final List<AdvanceQueryHandler> advanceQueryHandlers;
    @Value("${scheme-config.db.payout}")
    private String dbScheme;

    public Object detail(final TransactionDetailRequest request) {
        switch (request.getTransactionType()) {
            case PAY_OUT:
                return executePayoutDetailMapper(request);
            case PAY_IN:
                switch (request.getProductCode()) {
                    case BOLETO:
                    case PIX_QR_CODE:
                    case PIX:
                        return executePayinPixDetail(request);
                    case CARD:
                        return executePayinCardDetail(request);
                    case PAY_CASH:
                        return executePayCashDetail(request);
                    case TED:
                        return executePayinBankTransferDetail(request);
                    case NEQUI:
                    case PSE:
                    case SPEI_BANK_TRANSFER:
                        return executePayinSpeiDetail(request);
                    case SPEI_VA:
                        return executePayinSpeiVaDetail(request);
                    case BANK_TRANSFER:
                        return executePeBankTransferDetail(request);
                    default:
                        return executePayinDefaultDetail(request);
                }
            case MARKET_PLACE_ORDERS:
                return executeMarketPlaceDetail(request);
        }
        return null;
    }

    private PayoutDetail executePayoutDetailMapper(final TransactionDetailRequest request) {
        SourceNode configNode = sourceConfig.getPayout().get("normal");
        if (configNode == null || configNode.isIgnore()) {
            return null;
        }
        String collection = configNode.getCollection();
        String sourceTable = SchemeConsts.PAY_OUT_TRANSACTIONS_DB_SCHEME.equals(dbScheme) ?
                configNode.getTablePrefix() :
                configNode.getTablePrefix() + request.getMerchant().toLowerCase();
        Query query = Query.query(
                Criteria.where(configNode.getUniqueId()).is(request.getUniqueId())
                        .and("source.table").is(sourceTable)
        ).with(Sort.by(Sort.Order.asc("source.ts_ms"), Sort.Order.asc("op")));

        return getPayoutDetail(query, collection);
    }

    private PayoutDetail getPayoutDetail(Query query, String collection) {
        if (SchemeConsts.PAY_OUT_TRANSACTIONS_DB_SCHEME.equals(dbScheme)) {
            List<PayoutEventLog> payoutEventLogList =
                    mongoTemplate.find(query, PayoutEventLog.class, collection);
            return TransactionDetailsMapper.fromPayoutEventLogList(payoutEventLogList);
        } else {
            List<PayoutBackEventLog> payoutBackEventLogList =
                    mongoTemplate.find(query, PayoutBackEventLog.class, collection);
            return TransactionDetailsMapper.fromPayoutBackEventLogList(payoutBackEventLogList);
        }

    }

    private MarketPlaceDetail executeMarketPlaceDetail(final TransactionDetailRequest request) {
        SourceNode configNode = sourceConfig.getMarketplace().get("normal");
        if (configNode == null || configNode.isIgnore()) {
            return null;
        }
        String collection = configNode.getCollection();
        String sourceDB = configNode.getDb();
        String sourceTable = configNode.getTablePrefix() + request.getMerchant().toLowerCase();
        Query query = Query.query(
                Criteria.where(configNode.getUniqueId()).is(request.getUniqueId())
                        .and("source.db").is(sourceDB)
                        .and("source.table").is(sourceTable)
        ).with(Sort.by(Sort.Order.asc("source.ts_ms"), Sort.Order.asc("op")));
        List<MarketPlaceOrderEventLog> marketPlaceOrderEventLogs =
                mongoTemplate.find(query, MarketPlaceOrderEventLog.class, collection);
        return TransactionDetailsMapper.marketPlaceEventLogsToDetail(
                marketPlaceOrderEventLogs);
    }

    private PayinPixDetail executePayinPixDetail(final TransactionDetailRequest request) {
        return TransactionDetailsMapper.payinEventLogsToPixDetail(queryVirgoData(request));
    }

    private PayinCardDetail executePayinCardDetail(final TransactionDetailRequest request) {
        return TransactionDetailsMapper.payinEventLogsToCardDetail(queryVirgoData(request));
    }

    private PayCashDetail executePayCashDetail(final TransactionDetailRequest request) {
        return TransactionDetailsMapper.payinEventLogsToPayCashDetail(queryVirgoData(request));
    }

    private PayinBankTransferDetail executePayinBankTransferDetail(
            final TransactionDetailRequest request) {
        return TransactionDetailsMapper.payinEventLogsToTedDetail(queryVirgoData(request));
    }

    private PayinBankTransferDetail executePayinSpeiDetail(final TransactionDetailRequest request) {
        return TransactionDetailsMapper.payinEventLogsToSpeiDetail(queryVirgoData(request));
    }

    private BankTransferDetail executePeBankTransferDetail(final TransactionDetailRequest request) {
        return TransactionDetailsMapper.payinEventLogToBankTransferDetail(queryVirgoData(request));
    }

    private PayinSpeiVaDetail executePayinSpeiVaDetail(final TransactionDetailRequest request) {
        PayinSpeiVaDetail res = null;
        List<SubAccountPayBackEventLog> subAccountPayBackEventLogs = queryLiquidoData(request);
        if (!subAccountPayBackEventLogs.isEmpty()) {
            res = TransactionDetailsMapper.subAccountEventLogsToSpeiVaDetail(
                    subAccountPayBackEventLogs);
        } else {
            List<PayinEventLog> payinEventLogs = queryVirgoData(request);
            if (!payinEventLogs.isEmpty()) {
                res = TransactionDetailsMapper.payinEventLogsToSpeiVaDetail(payinEventLogs);
            }
        }

        if (res != null && res.getSubAccount() != null) {
            SubAccountEventLog subAccountEventLog = getSubAccountEventLog(
                    res.getSubAccount().getAccountId());
            if (subAccountEventLog != null) {
                res.getSubAccount().setAccountName(
                        subAccountEventLog.getAfter().getLegal_name());
                res.getSubAccount().setCreateTimestamp(
                        subAccountEventLog.getSource().getTs_ms() / 1000);
            } else {
                res.setSubAccount(null);
            }
        } else if (res != null) {
            res.setSubAccount(null);
        }

        return res;
    }

    private PayinDefaultDetail executePayinDefaultDetail(final TransactionDetailRequest request) {
        return TransactionDetailsMapper.payinEventLogsToDefaultDetail(queryVirgoData(request));
    }

    private List<PayinEventLog> queryVirgoData(final TransactionDetailRequest request) {
        SourceNode configNode = sourceConfig.getPayin().get("normal");
        if (configNode == null || configNode.isIgnore()) {
            return new ArrayList<>();
        }
        Query query = Query.query(
                Criteria.where(configNode.getUniqueId()).is(request.getUniqueId())
                        .and("source.db").is(configNode.getDb())
                        .and("after.merchant_name").is(request.getMerchant())
        ).with(Sort.by(Sort.Order.asc("source.ts_ms"), Sort.Order.asc("op")));
        List<PayinEventLog> payinEventLogList =
                mongoTemplate.find(query, PayinEventLog.class, configNode.getCollection());

        if (!payinEventLogList.isEmpty()) {
            return payinEventLogList;
        }

        configNode = sourceConfig.getPayin().get("backup");
        if (configNode == null || configNode.isIgnore()) {
            return new ArrayList<>();
        }
        return mongoTemplate.find(query, PayinEventLog.class, configNode.getCollection());
    }

    private List<SubAccountPayBackEventLog> queryLiquidoData(
            final TransactionDetailRequest request) {
        SourceNode configNode = sourceConfig.getSubAccount().get("normal");
        if (configNode == null || configNode.isIgnore()) {
            return new ArrayList<>();
        }
        Query query = Query.query(
                Criteria.where(configNode.getUniqueId()).is(request.getUniqueId())
                        .and("source.db").is(configNode.getDb())
                        .and("source.table")
                        .is(configNode.getTablePrefix() + request.getMerchant().toLowerCase())
        ).with(Sort.by(Sort.Order.asc("source.ts_ms"), Sort.Order.asc("op")));
        List<SubAccountPayBackEventLog> paymentSubAccountPayBackEventLogs =
                mongoTemplate.find(query, SubAccountPayBackEventLog.class,
                        configNode.getCollection());
        if (!paymentSubAccountPayBackEventLogs.isEmpty()) {
            return paymentSubAccountPayBackEventLogs;
        }

        configNode = sourceConfig.getSubAccount().get("backup");
        if (configNode == null || configNode.isIgnore()) {
            return new ArrayList<>();
        }
        return mongoTemplate.find(query, SubAccountPayBackEventLog.class,
                configNode.getCollection());
    }

    private SubAccountEventLog getSubAccountEventLog(String accountId) {
        Query query = Query.query(Criteria.where("after.account_id").is(accountId));
        List<SubAccountEventLog> eventLogs =
                mongoTemplate.find(query, SubAccountEventLog.class, "sub_account");
        if (!eventLogs.isEmpty()) {
            return eventLogs.get(0);
        }
        return null;
    }

    public PageResult<String> advanceQuery(final TransactionAdvanceQueryRequest request) {
        final AdvanceQueryHandler handler = advanceQueryHandlers.stream()
                .filter(v -> v.support(request))
                .findFirst()
                .orElse(null);

        final List<String> eligibleList = Objects.isNull(handler) ?
                Collections.emptyList() : handler.executeQuery(request);

        final int count = eligibleList.size();
        final int start =
                Math.max(0, Math.min(request.getPageSize() * (request.getPage() - 1), count));
        final int end = Math.min(start + request.getPageSize(), count);
        final List<String> results = eligibleList.subList(start, end);
        return PageResult.<String>builder()
                .totalCount(count)
                .countLimit(Long.MAX_VALUE)
                .pageSize(request.getPageSize())
                .page(request.getPage())
                .moreThanLimit(false)
                .results(results)
                .build();
    }

}
