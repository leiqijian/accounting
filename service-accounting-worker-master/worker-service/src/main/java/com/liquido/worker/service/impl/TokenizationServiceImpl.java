package com.liquido.worker.service.impl;

import java.util.Objects;

import com.liquido.core.common.security.Md5Util;
import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.core.common.utils.PageUtil;
import com.liquido.core.mvc.enums.SortTypeEnum;
import com.liquido.core.mvc.vo.PageVo;
import com.liquido.worker.pojo.dto.PageTokenizationDto;
import com.liquido.worker.pojo.entity.CardTokenization;
import com.liquido.worker.pojo.vo.PageTokenizationVo;
import com.liquido.worker.repository.TokenizationRepository;
import com.liquido.worker.service.TokenizationService;

import com.github.wenhao.jpa.PredicateBuilder;
import com.github.wenhao.jpa.Specifications;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Throwable.class)
public class TokenizationServiceImpl implements TokenizationService {

    private final TokenizationRepository tokenizationRepository;

    @Override
    public PageVo<PageTokenizationDto> pageTokenization(final PageTokenizationVo vo) {
        vo.setTokenMd5(
                StringUtils.isNotBlank(vo.getTokenId()) ? Md5Util.getMd5(vo.getTokenId()) : null);
        PredicateBuilder<CardTokenization> specBuilder = Specifications.<CardTokenization>and()
                .eq("merchantCode", vo.getMerchantCode())
                .eq(Objects.nonNull(vo.getCountryCode()), "countryCode", vo.getCountryCode())
                .eq(StringUtils.isNotBlank(vo.getTokenMd5()), "tokenMd5", vo.getTokenMd5())
                .eq(StringUtils.isNotBlank(vo.getLast4Digit()), "last4Digit", vo.getLast4Digit())
                .eq(StringUtils.isNotBlank(vo.getBin()), "bin", vo.getBin())
                .ge(Objects.nonNull(vo.getStartDate()), "tokenizationCreatedTimestamp",
                        LocalDateTimeUtil.utcToInstant(vo.getStartDate()))
                .le(Objects.nonNull(vo.getEndDate()), "tokenizationCreatedTimestamp",
                        LocalDateTimeUtil.utcToInstant(vo.getEndDate()))
                .like(StringUtils.isNotBlank(vo.getCardHolderName()), "cardHolderName",
                        String.format("%s%%", vo.getCardHolderName()));

        Sort.Order order = Sort.Order.desc("tokenizationCreatedTimestamp");
        if (ObjectUtils.isNotEmpty(vo.getSortField()) && ObjectUtils.isNotEmpty(vo.getSortType())) {
            order = SortTypeEnum.ASC == vo.getSortType() ? Sort.Order.asc(vo.getSortField())
                    : Sort.Order.desc(vo.getSortField());
        }

        final Page<CardTokenization> page =
                tokenizationRepository.findAll(specBuilder.build(),
                        PageRequest.of(vo.getPageNo() - 1, vo.getPageSize(), Sort.by(order)));

        return PageUtil.buildPage(page, vo, PageTokenizationDto.class);
    }
}
