package com.liquido.base.controller;

import java.util.List;
import javax.validation.Valid;

import com.liquido.base.api.CountryProductApi;
import com.liquido.base.pojo.dto.CountryProductDto;
import com.liquido.base.pojo.vo.CountryProductVo;
import com.liquido.base.pojo.vo.QueryCountrySupportedProductsVo;
import com.liquido.base.service.CountryProductService;
import com.liquido.core.mvc.dto.ResponseDto;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
public class CountryProductController implements CountryProductApi {

    private final CountryProductService countryProductService;

    @Override
    @PostMapping("/base/country-product/add")
    public ResponseDto<Long> addCountryProduct(@Valid @RequestBody final CountryProductVo vo) {
        return ResponseDto.success(countryProductService.save(vo));
    }

    @Override
    @PostMapping("/base/country-product/list")
    public ResponseDto<List<CountryProductDto>> listCountrySupportedProducts(
            @Valid @RequestBody final QueryCountrySupportedProductsVo vo) {
        return ResponseDto.success(countryProductService.listCountrySupportedProducts(vo));
    }

}
