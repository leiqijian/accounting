package com.liquido.base.service;

import java.util.List;

import com.liquido.base.pojo.dto.CountryProductDto;
import com.liquido.base.pojo.vo.CountryProductVo;
import com.liquido.base.pojo.vo.EditCountryProductVo;
import com.liquido.base.pojo.vo.QueryCountrySupportedProductsVo;

public interface CountryProductService {

    Long save(CountryProductVo vo);

    void delete(Long id);

    void update(Long id, EditCountryProductVo vo);

    List<CountryProductDto> listCountrySupportedProducts(QueryCountrySupportedProductsVo vo);

}
