package com.liquido.base.service.impl;

import java.time.LocalTime;
import java.util.stream.Collectors;

import com.liquido.base.pojo.dto.PageMerchantMessageDto;
import com.liquido.base.pojo.entity.MerchantMessage;
import com.liquido.base.pojo.vo.AddMerchantMessageVo;
import com.liquido.base.pojo.vo.PageMerchantMessageVo;
import com.liquido.base.repository.MerchantMessageRepository;
import com.liquido.base.service.MerchantMessageService;
import com.liquido.core.common.utils.BeanCopierUtil;
import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.core.common.utils.LocalDateUtil;
import com.liquido.core.mvc.vo.PageVo;

import com.github.wenhao.jpa.Specifications;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class MerchantMessageServiceImpl implements MerchantMessageService {
    private final MerchantMessageRepository merchantMessageRepository;

    @Override
    public void saveMessage(AddMerchantMessageVo vo) {
        merchantMessageRepository.save(MerchantMessage.builder()
                .merchantId(vo.getMerchantId())
                .countryCode(vo.getCountryCode())
                .messageSourceType(vo.getMessageSourceType())
                .messageType(vo.getMessageType())
                .message(vo.getMessage())
                .eventTime(LocalDateTimeUtil.nowUtc())
                .build());
    }

    @Override
    public PageVo<PageMerchantMessageDto> pageMerchantMessage(final PageMerchantMessageVo vo) {
        final Specification<MerchantMessage> spec = Specifications.<MerchantMessage>and()
                .eq("merchantId", vo.getMerchantId())
                .eq("countryCode", vo.getCountry())
                .ge("eventTime",
                        LocalDateUtil.dateToUtcDatetime(vo.getDate(),
                                LocalTime.MIN, vo.getTimeZone()))
                .lt("eventTime",
                        LocalDateUtil.dateToUtcDatetime(vo.getDate().plusDays(1),
                                LocalTime.MIN, vo.getTimeZone())).build();

        final Page<MerchantMessage> page = merchantMessageRepository.findAll(spec,
                PageRequest.of(vo.getPageNo() - 1, vo.getPageSize(),
                        Sort.by(Sort.Order.asc("eventTime"))));

        return new PageVo<>(vo.getPageNo(), vo.getPageSize(),
                page.getTotalElements(),
                page.getContent().stream().map(x -> {
                    final PageMerchantMessageDto bo = new PageMerchantMessageDto();
                    BeanCopierUtil.copyProperties(x, bo);
                    return bo;
                }).collect(Collectors.toList()));
    }
}

