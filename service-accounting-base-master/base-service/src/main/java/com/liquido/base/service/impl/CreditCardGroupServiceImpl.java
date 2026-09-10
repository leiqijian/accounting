package com.liquido.base.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.liquido.base.enums.CreditCardGroupCodeEnum;
import com.liquido.base.pojo.dto.CreditCardGroupDto;
import com.liquido.base.pojo.entity.CreditCardGroup;
import com.liquido.base.pojo.entity.QCreditCardGroup;
import com.liquido.base.repository.CreditCardGroupRepository;
import com.liquido.base.service.CreditCardGroupService;
import com.liquido.core.common.utils.BeanCopierUtil;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
public class CreditCardGroupServiceImpl implements CreditCardGroupService {

    private final JPAQueryFactory jpaQueryFactory;
    private final CreditCardGroupRepository creditCardGroupRepository;

    @Override
    public List<CreditCardGroupDto> finalAll() {
        final List<CreditCardGroup> list = creditCardGroupRepository.findAll();
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }

        return BeanCopierUtil.copyPropertyList(list, CreditCardGroupDto.class);
    }

    @Override
    public List<CreditCardGroupDto> finalByGroupCode(CreditCardGroupCodeEnum groupCode) {
        if (Objects.isNull(groupCode)) {
            return Collections.emptyList();
        }

        final List<CreditCardGroup> list = creditCardGroupRepository.findAllByGroupCode(groupCode);
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }

        return BeanCopierUtil.copyPropertyList(list, CreditCardGroupDto.class);
    }

    @Override
    public CreditCardGroupDto finalByIinRange(Integer iinValue) {
        QCreditCardGroup entity = QCreditCardGroup.creditCardGroup;
        final CreditCardGroup creditCardGroup = jpaQueryFactory.select(entity).from(entity)
                .where(entity.iinBegin.loe(iinValue).and(entity.iinEnd.goe(iinValue))).fetchOne();

        if (Objects.nonNull(creditCardGroup)) {
            return BeanCopierUtil.copyProperties(creditCardGroup, CreditCardGroupDto.class);
        }

        return null;
    }
}
