package com.liquido.worker.service.calculate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.worker.exception.WorkerExceptionCode;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

/**
 * PayIn or Payout transaction provider factory
 */
@Slf4j
@Component
public class TransactionProviderFactory {

    private static Map<TransactionTypeCodeEnum, TransactionStrategy> transactionProviderMap =
            Maps.newHashMap();

    public TransactionProviderFactory(
            final ObjectProvider<List<TransactionStrategy>> provider) {

        final List<TransactionStrategy> strategyList = provider.getIfAvailable();
        if (CollectionUtils.isNotEmpty(strategyList)) {
            transactionProviderMap = strategyList.stream().collect(
                    Collectors.toMap(TransactionStrategy::getStrategy, objSelf -> objSelf,
                            (k1, k2) -> k1));
        }
    }

    public <T extends TransactionStrategy> T getProviderFactory(
            final String transactionTypeCode) {

        if (StringUtils.isBlank(transactionTypeCode)) {
            throw WorkerExceptionCode.TRANSACTION_TYPE_UNDEFINED.exception(transactionTypeCode);
        }

        return this.getProviderFactory(TransactionTypeCodeEnum.parse(transactionTypeCode));
    }

    public <T extends TransactionStrategy> T getProviderFactory(
            final TransactionTypeCodeEnum transactionType) {

        if (transactionType == null) {
            throw WorkerExceptionCode.TRANSACTION_TYPE_UNDEFINED.exception(transactionType);
        }

        return (T) transactionProviderMap.get(transactionType);
    }
}
