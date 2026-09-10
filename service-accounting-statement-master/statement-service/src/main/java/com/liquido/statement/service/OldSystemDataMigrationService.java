package com.liquido.statement.service;

import java.util.List;

import com.liquido.statement.pojo.dto.BillDetailDto;
import com.liquido.statement.pojo.vo.QueryMerchantBillDetailVo;

public interface OldSystemDataMigrationService {
    List<BillDetailDto> importBillDetails(final QueryMerchantBillDetailVo vo);
}
