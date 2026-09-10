package com.liquido.base.service;


import java.util.List;
import java.util.Set;

import com.liquido.base.pojo.dto.MerchantDto;
import com.liquido.base.pojo.dto.MerchantWeightDto;
import com.liquido.base.pojo.vo.AddMerchantVo;
import com.liquido.base.pojo.vo.EditMerchantVo;
import com.liquido.base.pojo.vo.ListMerchantVo;
import com.liquido.base.pojo.vo.PageMerchantVo;
import com.liquido.base.pojo.vo.QueryMerchantVo;
import com.liquido.core.mvc.vo.PageVo;

public interface MerchantService {

    MerchantDto save(final AddMerchantVo vo);

    void update(final Long id, final EditMerchantVo vo);

    List<MerchantDto> queryAllMerchant();

    List<MerchantDto> queryAllMerchantWithEffectiveReportWeight();

    List<MerchantDto> listMerchant(final ListMerchantVo vo);

    PageVo<MerchantDto> pageMerchant(final PageMerchantVo vo);

    MerchantDto getEffectiveMerchantInfo(final QueryMerchantVo vo);

    boolean existsByMerchantCode(final String merchantCode);

    List<Long> queryLowerWeightMerchantIds();

    List<MerchantWeightDto> queryAllMerchantWeight();

    Set<Long> querySubMerchantDividedBillMerchantIds();

}
