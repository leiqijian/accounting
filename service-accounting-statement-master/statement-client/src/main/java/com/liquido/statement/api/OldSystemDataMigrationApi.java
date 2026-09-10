package com.liquido.statement.api;

import java.util.List;
import javax.validation.Valid;

import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.statement.pojo.dto.BillDetailDto;
import com.liquido.statement.pojo.vo.QueryMerchantBillDetailVo;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface OldSystemDataMigrationApi {
    @PostMapping("/statement/liquido/v1/bill-details/import")
    ResponseDto<List<BillDetailDto>> importBillDetails(
            @RequestBody @Valid final QueryMerchantBillDetailVo vo);
}
