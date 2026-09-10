package com.liquido.statement.controller;

import java.util.List;
import javax.validation.Valid;

import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.statement.api.OldSystemDataMigrationApi;
import com.liquido.statement.pojo.dto.BillDetailDto;
import com.liquido.statement.pojo.vo.QueryMerchantBillDetailVo;
import com.liquido.statement.service.OldSystemDataMigrationService;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
public class OldSystemDataMigrationController implements OldSystemDataMigrationApi {

    private final OldSystemDataMigrationService migrationService;

    @Override
    @PostMapping("/statement/liquido/v1/bill-details/import")
    public ResponseDto<List<BillDetailDto>> importBillDetails(
            @RequestBody @Valid final QueryMerchantBillDetailVo vo) {
        return ResponseDto.success(migrationService.importBillDetails(vo));
    }

}
