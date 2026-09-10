package com.liquido.base.api;

import java.util.List;
import javax.validation.Valid;

import com.liquido.base.pojo.dto.SubMerchantDto;
import com.liquido.base.pojo.vo.AddSubMerchantVo;
import com.liquido.base.pojo.vo.QuerySubMerchantInfoVo;
import com.liquido.base.pojo.vo.QuerySubMerchantListVo;
import com.liquido.base.pojo.vo.QuerySubMerchantVo;
import com.liquido.core.mvc.dto.ResponseDto;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface SubMerchantApi {

    @PostMapping("/base/sub-merchant/add")
    ResponseDto<Void> addSubMerchant(@RequestBody @Valid final AddSubMerchantVo vo);

    @PostMapping("/base/subMerchant/list/query/by/sub-merchant/id")
    ResponseDto<List<SubMerchantDto>> queryBySubMerchantId(
            @RequestBody @Valid final QuerySubMerchantListVo vo);

    @PostMapping("/base/sub-merchant/exists")
    ResponseDto<Boolean> existsByMerchantId(
            @RequestBody @Valid final QuerySubMerchantVo vo);

    @PostMapping("/base/sub-merchant/list/query/by/merchant/id")
    ResponseDto<List<SubMerchantDto>> querySubMerchantListByMerchantId(
            @RequestBody @Valid final QuerySubMerchantVo vo);

    @PostMapping("/base/sub-merchant/query")
    ResponseDto<SubMerchantDto> querySubMerchant(
            @RequestBody @Valid final QuerySubMerchantInfoVo vo);
}
