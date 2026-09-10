package com.liquido.base.service.impl;

import static com.liquido.base.pojo.entity.QAccountProductVersion.accountProductVersion;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.liquido.base.pojo.dto.AccountProductVersionDto;
import com.liquido.base.pojo.entity.AccountProductVersion;
import com.liquido.base.pojo.vo.AccountProductVersionVo;
import com.liquido.base.pojo.vo.EditAccountProductVersionMonthlyFlagVo;
import com.liquido.base.pojo.vo.QueryAccountProductVersionVo;
import com.liquido.base.repository.AccountProductVersionRepository;
import com.liquido.base.service.AccountProductVersionService;
import com.liquido.core.common.utils.BeanCopierUtil;
import com.liquido.core.common.utils.LocalDateTimeUtil;

import com.github.wenhao.jpa.PredicateBuilder;
import com.github.wenhao.jpa.Specifications;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountProductVersionServiceImpl implements AccountProductVersionService {

    private final AccountProductVersionRepository accountProductVersionRepository;
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Long save(final AccountProductVersionVo vo) {
        final AccountProductVersion bean = voToBean(vo);
        final LocalDateTime now = LocalDateTimeUtil.nowUtc();
        bean.setCreatedTime(now);
        bean.setUpdatedTime(now);
        return accountProductVersionRepository.save(bean).getId();
    }

    @Override
    public List<AccountProductVersionDto> saveAll(final List<AccountProductVersionVo> listVo) {
        if (CollectionUtils.isEmpty(listVo)) {
            return Collections.emptyList();
        }
        final LocalDateTime now = LocalDateTimeUtil.nowUtc();
        final List<AccountProductVersion> listBean =
                listVo.stream().map(x -> {
                    final AccountProductVersion bean = voToBean(x);
                    bean.setCreatedTime(now);
                    bean.setUpdatedTime(now);
                    return bean;
                }).collect(Collectors.toList());
        return accountProductVersionRepository.saveAll(listBean).stream().map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AccountProductVersionDto> listAccountProductVersion(
            final QueryAccountProductVersionVo vo) {

        final List<AccountProductVersion> resultList = new ArrayList<>();
        // because vo.getAccountProductIds() number>1000, 'in' query need controller in number
        if (ObjectUtils.isNotEmpty(vo.getAccountProductIds())) {
            resultList.addAll(ListUtils.partition(vo.getAccountProductIds(), 400).stream()
                    .flatMap(accountProductIds -> {
                        final PredicateBuilder<AccountProductVersion>
                                spec = buildQueryPredicate(vo);
                        spec.in("accountProductId",
                                ListUtils.emptyIfNull(accountProductIds).toArray());
                        return accountProductVersionRepository.findAll(spec.build()).stream();
                    }).collect(Collectors.toList()));
        } else {
            resultList.addAll(
                    accountProductVersionRepository.findAll(buildQueryPredicate(vo).build()));
        }
        return toDto(resultList);
    }

    private PredicateBuilder<AccountProductVersion> buildQueryPredicate(
            final QueryAccountProductVersionVo vo) {
        final PredicateBuilder<AccountProductVersion> spec =
                Specifications.<AccountProductVersion>and()
                        .eq(Objects.nonNull(vo.getActiveMonth()),
                                "activeMonth", vo.getActiveMonth())
                        .eq(Objects.nonNull(vo.getAccountId()),
                                "accountId", vo.getAccountId())
                        .eq(Objects.nonNull(vo.getAccountProductId()),
                                "accountProductId", vo.getAccountProductId())
                        .eq(Objects.nonNull(vo.getMonthlyFlag()),
                                "monthlyFlag", vo.getMonthlyFlag());
        if (Objects.nonNull(vo.getActiveDate())) {
            spec.le("startDate", vo.getActiveDate());
            spec.ge("endDate", vo.getActiveDate());
        }
        return spec;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Long updateAccountProductVersionMonthFlag(
            final EditAccountProductVersionMonthlyFlagVo vo) {
        return jpaQueryFactory.update(accountProductVersion)
                .set(accountProductVersion.monthlyFlag, vo.getMonthFlag())
                .where(accountProductVersion.id.in(vo.getAccountProductVersionIds())).execute();
    }


    private AccountProductVersion voToBean(final AccountProductVersionVo vo) {
        final AccountProductVersion bean = new AccountProductVersion();
        BeanCopierUtil.copyProperties(vo, bean);
        return bean;
    }

    private AccountProductVersionDto toDto(final AccountProductVersion original) {
        final AccountProductVersionDto bean = new AccountProductVersionDto();
        BeanCopierUtil.copyProperties(original, bean);
        return bean;
    }

    private List<AccountProductVersionDto> toDto(final List<AccountProductVersion> originalList) {
        return originalList.stream().map(this::toDto).collect(Collectors.toList());
    }

}
