package com.liquido.aqueducts.service;

import java.util.List;

import com.liquido.aqueducts.commons.PageResult;
import com.liquido.aqueducts.commons.constants.MongoDbConsts;
import com.liquido.aqueducts.mapper.TokenVaultMapper;
import com.liquido.aqueducts.vo.document.eventlog.TokenVaultEventLog;
import com.liquido.aqueducts.vo.response.eventlog.TokenVaultItem;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class TokenService {

    private final MongoTemplate mongoTemplate;
    private final TokenVaultMapper tokenVaultMapper;

    public PageResult<TokenVaultItem> list(long from, long to) {
        final Query query = Query.query(
                Criteria.where("_id")
                        .gt(new ObjectId(
                                Long.toHexString(from) + MongoDbConsts.OBJECT_ID_SUFFIX_ZERO))
                        .lte(new ObjectId(Long.toHexString(to) + MongoDbConsts.OBJECT_ID_SUFFIX_F))
                        .and("op").ne("d")
        );
        final List<TokenVaultEventLog> tokenVaultEventLogs =
                mongoTemplate.find(query, TokenVaultEventLog.class, "token_vault");

        final List<TokenVaultItem> tokenVaultItems =
                tokenVaultMapper.eventLogToTokenVault(tokenVaultEventLogs);

        return PageResult.<TokenVaultItem>builder()
                .totalCount(tokenVaultItems.size())
                .countLimit(Long.MAX_VALUE)
                .pageSize(Long.MAX_VALUE)
                .page(1L)
                .moreThanLimit(false)
                .results(tokenVaultItems)
                .build();
    }

}
