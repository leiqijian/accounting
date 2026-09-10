package com.liquido.statement.service;

import com.liquido.statement.pojo.dto.PageAccountDto;

public interface BalanceSnapshotService {

    void saveOrUpdate(final PageAccountDto dto);
}
