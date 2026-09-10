package com.liquido.base.service.impl;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.liquido.base.pojo.dto.CountryProductDto;
import com.liquido.base.pojo.entity.CountryProduct;
import com.liquido.base.pojo.vo.CountryProductVo;
import com.liquido.base.pojo.vo.EditCountryProductVo;
import com.liquido.base.pojo.vo.QueryCountrySupportedProductsVo;
import com.liquido.base.repository.CountryProductRepository;
import com.liquido.base.service.CountryProductService;
import com.liquido.core.common.exception.CommonExceptionCode;
import com.liquido.core.common.utils.BeanCopierUtil;

import com.github.wenhao.jpa.PredicateBuilder;
import com.github.wenhao.jpa.Specifications;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CountryProductServiceImpl implements CountryProductService {

    private final CountryProductRepository countryProductRepository;

    public Long save(final CountryProductVo vo) {
        final CountryProduct bean = new CountryProduct();
        BeanCopierUtil.copyProperties(vo, bean);
        return countryProductRepository.save(bean).getId();
    }

    public void delete(final Long id) {
        countryProductRepository.deleteById(id);
    }

    public void update(final Long id, final EditCountryProductVo vo) {
        final CountryProduct bean = requireOne(id);
        BeanCopierUtil.copyProperties(vo, bean);
        countryProductRepository.save(bean);
    }

    @Override
    public List<CountryProductDto> listCountrySupportedProducts(
            final QueryCountrySupportedProductsVo vo) {

        final PredicateBuilder<CountryProduct> spec = Specifications.<CountryProduct>and()
                .eq(Objects.nonNull(vo.getCountry()), "countryCode", vo.getCountry())
                .eq(Objects.nonNull(vo.getTransactionType()), "transactionTypeCode",
                        vo.getTransactionType());

        return toDto(countryProductRepository.findAll(spec.build()));
    }

    private CountryProduct requireOne(final Long id) {
        return countryProductRepository.findById(id)
                .orElseThrow(CommonExceptionCode.DATA_NOT_FOUND::exception);
    }

    private CountryProductDto toDto(final CountryProduct original) {
        final CountryProductDto bean = new CountryProductDto();
        BeanCopierUtil.copyProperties(original, bean);
        return bean;
    }

    private List<CountryProductDto> toDto(final List<CountryProduct> originalList) {
        return originalList.stream().map(this::toDto).collect(Collectors.toList());
    }

}
