package com.liquido.base.repository;

import java.util.List;

import com.liquido.base.pojo.entity.Dictionary;

import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaSpecificationExecutor;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphQuerydslPredicateExecutor;

public interface DictionaryRepository extends
        EntityGraphJpaRepository<Dictionary<String>, Long>,
        EntityGraphJpaSpecificationExecutor<Dictionary<String>>,
        EntityGraphQuerydslPredicateExecutor<Dictionary<String>> {

    Dictionary<String> findByTypeAndKey(final String type, final String key);

    List<Dictionary<String>> findAllByType(final String type);
}
