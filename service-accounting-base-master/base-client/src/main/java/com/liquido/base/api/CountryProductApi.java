package com.liquido.base.api;

import java.util.List;
import javax.validation.Valid;

import com.liquido.base.pojo.dto.CountryProductDto;
import com.liquido.base.pojo.vo.CountryProductVo;
import com.liquido.base.pojo.vo.QueryCountrySupportedProductsVo;
import com.liquido.core.mvc.dto.ResponseDto;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface CountryProductApi {

    /**
     * Add CountryProduct
     *
     * @param vo
     *
     * @return database insert id
     */
    @PostMapping("/base/country-product/add")
    ResponseDto<Long> addCountryProduct(@Valid @RequestBody CountryProductVo vo);

    @PostMapping("/base/country-product/list")
    ResponseDto<List<CountryProductDto>> listCountrySupportedProducts(
            @Valid @RequestBody final QueryCountrySupportedProductsVo vo);

}
