package com.liquido.base.service;


import java.util.List;

import com.liquido.base.pojo.dto.SubMerchantDto;
import com.liquido.base.pojo.vo.AddSubMerchantVo;
import com.liquido.base.pojo.vo.QuerySubMerchantInfoVo;
import com.liquido.base.pojo.vo.QuerySubMerchantListVo;
import com.liquido.base.pojo.vo.QuerySubMerchantVo;

public interface SubMerchantService {

    void save(final AddSubMerchantVo vo);

    List<SubMerchantDto> queryBySubMerchantId(final QuerySubMerchantListVo vo);

    boolean existsByMerchantId(final QuerySubMerchantVo vo);

    List<SubMerchantDto> querySubMerchantListByMerchantId(final QuerySubMerchantVo vo);

    SubMerchantDto querySubMerchant(QuerySubMerchantInfoVo vo);
}
