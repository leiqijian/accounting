package com.liquido.base.api;

import java.util.List;
import java.util.Set;
import javax.validation.Valid;

import com.liquido.base.pojo.dto.MerchantDto;
import com.liquido.base.pojo.dto.MerchantWeightDto;
import com.liquido.base.pojo.vo.AddMerchantVo;
import com.liquido.base.pojo.vo.EditMerchantVo;
import com.liquido.base.pojo.vo.ListMerchantVo;
import com.liquido.base.pojo.vo.PageMerchantVo;
import com.liquido.base.pojo.vo.QueryMerchantCodeVo;
import com.liquido.base.pojo.vo.QueryMerchantVo;
import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.core.mvc.vo.PageVo;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface MerchantApi {

    @PostMapping("/base/merchant/add")
    ResponseDto<MerchantDto> addMerchant(@RequestBody @Valid final AddMerchantVo vo);

    @PostMapping("/base/merchant/update")
    ResponseDto<Void> updateMerchant(@RequestBody @Valid final EditMerchantVo vo);

    @PostMapping("/base/merchant/all/query")
    ResponseDto<List<MerchantDto>> queryAllMerchant();

    @PostMapping("/base/merchant/all/effective-report-weight/query")
    ResponseDto<List<MerchantDto>> queryAllMerchantWithEffectiveReportWeight();

    @PostMapping("/base/merchant/list")
    ResponseDto<List<MerchantDto>> listMerchant(@RequestBody @Valid final ListMerchantVo vo);

    @PostMapping("/base/merchant/page")
    ResponseDto<PageVo<MerchantDto>> pageMerchant(@RequestBody @Valid final PageMerchantVo vo);

    @PostMapping("/base/merchant/info")
    ResponseDto<MerchantDto> getEffectiveMerchantInfo(@RequestBody @Valid final QueryMerchantVo vo);

    @PostMapping("/base/merchant/exists/by/code")
    ResponseDto<Boolean> existsByMerchantCode(@RequestBody @Valid final QueryMerchantCodeVo vo);

    @PostMapping("/base/merchant/lower-weight/query")
    ResponseDto<List<Long>> queryLowerWeightMerchantIds();

    @PostMapping("/base/merchant/weight/query")
    ResponseDto<List<MerchantWeightDto>> queryAllMerchantWeight();

    @PostMapping("/base/merchant/divide-bill/merchantIds/query")
    ResponseDto<Set<Long>> querySubMerchantDividedBillMerchantIds();
}
