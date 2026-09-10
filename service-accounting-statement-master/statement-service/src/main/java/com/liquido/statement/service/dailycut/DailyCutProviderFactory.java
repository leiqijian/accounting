package com.liquido.statement.service.dailycut;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.statement.exception.StatementExceptionCode;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * PayIn or Payout settlement provider factory
 */
@Slf4j
@Component
public class DailyCutProviderFactory {

    private static final Map<TransactionTypeCodeEnum, DailyCutHandlerService> providerMap =
            Maps.newConcurrentMap();

    private DailyCutProviderFactory(
            final ObjectProvider<List<DailyCutHandlerService>> provider) {

        final List<DailyCutHandlerService> strategyList = provider.getIfAvailable();
        if (!CollectionUtils.isEmpty(strategyList)) {
            providerMap.putAll(strategyList.stream().collect(
                    Collectors.toMap(DailyCutHandlerService::getStrategy,
                            objSelf -> objSelf, (k1, k2) -> k1)));
        }
    }

    public <T extends DailyCutHandlerService> T getProviderFactory(
            final String transactionTypeCode) {

        if (StringUtils.isBlank(transactionTypeCode)) {
            throw StatementExceptionCode.TRANSACTION_TYPE_UNDEFINED.exception();
        }

        return this.getProviderFactory(TransactionTypeCodeEnum.parse(transactionTypeCode));
    }

    public <T extends DailyCutHandlerService> T getProviderFactory(
            final TransactionTypeCodeEnum transactionType) {

        if (transactionType == null) {
            throw StatementExceptionCode.TRANSACTION_TYPE_UNDEFINED.exception();
        }

        return (T) providerMap.get(transactionType);
    }
}
