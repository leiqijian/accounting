package com.liquido.statement.service;

import com.liquido.statement.pojo.entity.AccountTransferRecord;

public interface AccountTransferRecordService {

    AccountTransferRecord saveTransferOrder(final AccountTransferRecord entity);
}
