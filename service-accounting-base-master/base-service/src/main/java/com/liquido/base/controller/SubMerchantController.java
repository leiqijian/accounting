package com.liquido.base.controller;

import java.util.List;
import javax.validation.Valid;

import com.liquido.base.api.SubMerchantApi;
import com.liquido.base.pojo.dto.SubMerchantDto;
import com.liquido.base.pojo.vo.AddSubMerchantVo;
import com.liquido.base.pojo.vo.QuerySubMerchantInfoVo;
import com.liquido.base.pojo.vo.QuerySubMerchantListVo;
import com.liquido.base.pojo.vo.QuerySubMerchantVo;
import com.liquido.base.service.SubMerchantService;
import com.liquido.core.mvc.dto.ResponseDto;

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
public class SubMerchantController implements SubMerchantApi {

    private final SubMerchantService subMerchantService;

    @PostMapping("/base/sub-merchant/add")
    public ResponseDto<Void> addSubMerchant(
            @RequestBody @Valid final AddSubMerchantVo vo) {
        subMerchantService.save(vo);
        return ResponseDto.success();
    }

    @PostMapping("/base/subMerchant/list/query/by/sub-merchant/id")
    public ResponseDto<List<SubMerchantDto>> queryBySubMerchantId(
            @RequestBody @Valid final QuerySubMerchantListVo vo) {
        return ResponseDto.success(subMerchantService.queryBySubMerchantId(vo));
    }

    @PostMapping("/base/sub-merchant/exists")
    public ResponseDto<Boolean> existsByMerchantId(
            @RequestBody @Valid final QuerySubMerchantVo vo) {
        return ResponseDto.success(subMerchantService.existsByMerchantId(vo));
    }

    @PostMapping("/base/sub-merchant/list/query/by/merchant/id")
    public ResponseDto<List<SubMerchantDto>> querySubMerchantListByMerchantId(
            @RequestBody @Valid final QuerySubMerchantVo vo) {
        return ResponseDto.success(subMerchantService.querySubMerchantListByMerchantId(vo));
    }

    @PostMapping("/base/sub-merchant/query")
    public ResponseDto<SubMerchantDto> querySubMerchant(
            @RequestBody @Valid final QuerySubMerchantInfoVo vo) {
        return ResponseDto.success(subMerchantService.querySubMerchant(vo));
    }

}
