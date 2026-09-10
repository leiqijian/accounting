package com.liquido.statement.service.impl;

import com.liquido.statement.pojo.entity.AccountTransferRecord;
import com.liquido.statement.repository.AccountTransferRecordRepository;
import com.liquido.statement.service.AccountTransferRecordService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountTransferRecordServiceImpl implements AccountTransferRecordService {
    private final AccountTransferRecordRepository repository;

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public AccountTransferRecord saveTransferOrder(final AccountTransferRecord order) {
        return repository.saveAndFlush(order);
    }
}
