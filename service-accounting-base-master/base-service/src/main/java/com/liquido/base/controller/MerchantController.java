package com.liquido.base.controller;

import java.util.List;
import java.util.Set;
import javax.validation.Valid;

import com.liquido.base.api.MerchantApi;
import com.liquido.base.pojo.dto.MerchantDto;
import com.liquido.base.pojo.dto.MerchantWeightDto;
import com.liquido.base.pojo.vo.AddMerchantVo;
import com.liquido.base.pojo.vo.EditMerchantVo;
import com.liquido.base.pojo.vo.ListMerchantVo;
import com.liquido.base.pojo.vo.PageMerchantVo;
import com.liquido.base.pojo.vo.QueryMerchantCodeVo;
import com.liquido.base.pojo.vo.QueryMerchantVo;
import com.liquido.base.service.MerchantService;
import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.core.mvc.vo.PageVo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class MerchantController implements MerchantApi {

    private final MerchantService merchantService;

    @PostMapping("/base/merchant/add")
    public ResponseDto<MerchantDto> addMerchant(@RequestBody @Valid final AddMerchantVo vo) {
        return ResponseDto.success(merchantService.save(vo));
    }

    @PostMapping("/base/merchant/update")
    public ResponseDto<Void> updateMerchant(@RequestBody @Valid final EditMerchantVo vo) {
        merchantService.update(vo.getId(), vo);
        return ResponseDto.success();
    }

    @PostMapping("/base/merchant/all/query")
    public ResponseDto<List<MerchantDto>> queryAllMerchant() {
        return ResponseDto.success(merchantService.queryAllMerchant());
    }

    @PostMapping("/base/merchant/all/effective-report-weight/query")
    public ResponseDto<List<MerchantDto>> queryAllMerchantWithEffectiveReportWeight() {
        return ResponseDto.success(merchantService.queryAllMerchantWithEffectiveReportWeight());
    }

    @PostMapping("/base/merchant/list")
    public ResponseDto<List<MerchantDto>> listMerchant(
            @RequestBody @Valid final ListMerchantVo vo) {
        return ResponseDto.success(merchantService.listMerchant(vo));
    }

    @PostMapping("/base/merchant/page")
    public ResponseDto<PageVo<MerchantDto>> pageMerchant(
            @RequestBody @Valid final PageMerchantVo vo) {
        return ResponseDto.success(merchantService.pageMerchant(vo));
    }

    @PostMapping("/base/merchant/info")
    public ResponseDto<MerchantDto> getEffectiveMerchantInfo(
            @RequestBody @Valid final QueryMerchantVo vo) {
        return ResponseDto.success(merchantService.getEffectiveMerchantInfo(vo));
    }

    @Override
    @PostMapping("/base/merchant/exists/by/code")
    public ResponseDto<Boolean> existsByMerchantCode(
            @RequestBody @Valid final QueryMerchantCodeVo vo) {
        return ResponseDto.success(merchantService.existsByMerchantCode(vo.getMerchantCode()));
    }

    @PostMapping("/base/merchant/lower-weight/query")
    public ResponseDto<List<Long>> queryLowerWeightMerchantIds() {
        return ResponseDto.success(merchantService.queryLowerWeightMerchantIds());
    }

    @Override
    @PostMapping("/base/merchant/weight/query")
    public ResponseDto<List<MerchantWeightDto>> queryAllMerchantWeight() {
        return ResponseDto.success(merchantService.queryAllMerchantWeight());
    }

    @Override
    @PostMapping("/base/merchant/divide-bill/merchantIds/query")
    public ResponseDto<Set<Long>> querySubMerchantDividedBillMerchantIds() {
        return ResponseDto.success(merchantService.querySubMerchantDividedBillMerchantIds());
    }

}
