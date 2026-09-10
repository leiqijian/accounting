package com.liquido.base.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.base.pojo.dto.WorkingDayDto;
import com.liquido.base.pojo.entity.QWorkingDay;
import com.liquido.base.pojo.entity.WorkingDay;
import com.liquido.base.pojo.vo.InitWorkingDayVo;
import com.liquido.base.pojo.vo.QueryWorkingDayVo;
import com.liquido.base.repository.WorkingDayRepository;
import com.liquido.base.service.WorkingDayService;
import com.liquido.core.common.snowflake.SnowflakeIdUtil;
import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.core.common.utils.LocalDateUtil;

import com.google.common.collect.Lists;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkingDayServiceImpl implements WorkingDayService {

    private final JPAQueryFactory jpaQueryFactory;
    private final WorkingDayRepository workingDayRepository;

    /**
     * initWorkingDay
     *
     * @param vo
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void initWorkingDay(final InitWorkingDayVo vo) {

        final LocalDate firstDayOfYear = LocalDateUtil.formatToLocalDate(vo.getYear() + "-01-01");
        final LocalDate lastDayOfYear = LocalDateUtil.formatToLocalDate(vo.getYear() + "-12-31");

        int i = 0;
        LocalDate date = firstDayOfYear;
        final List<WorkingDay> workdayList = Lists.newArrayList();
        while (lastDayOfYear.isAfter(date)) {
            date = firstDayOfYear.plusDays(i);
            workdayList.add(WorkingDay.builder()
                    .workDate(date)
                    .weekday(date.getDayOfWeek().getValue())
                    .workday(date.getDayOfWeek().getValue() <= 5)
                    .createdTime(LocalDateTimeUtil.nowUtc())
                    .updatedTime(LocalDateTimeUtil.nowUtc())
                    .version(0)
                    .delFlag(false)
                    .remark("")
                    .build());
            i++;
        }

        if (Objects.nonNull(vo.getCountry())) {
            workdayList.forEach(item -> {
                item.setId(SnowflakeIdUtil.generate());
                item.setCountry(vo.getCountry());
            });

            workingDayRepository.saveAllAndFlush(workdayList);
        } else {

            // init all country workday
            for (final CountryCodeEnum country : CountryCodeEnum.values()) {
                workdayList.forEach(item -> {
                    item.setId(SnowflakeIdUtil.generate());
                    item.setCountry(country);
                });

                workingDayRepository.saveAllAndFlush(workdayList);
            }
        }
    }

    @Override
    public List<WorkingDayDto> queryWorkingDay(final QueryWorkingDayVo vo) {
        final QWorkingDay entity = QWorkingDay.workingDay;

        final LocalDate firstDayOfYear =
                LocalDateUtil.formatToLocalDate(vo.getYear() + "-01-01");
        final LocalDate lastDayOfYear =
                LocalDateUtil.formatToLocalDate((vo.getYear() + 1) + "-12-31");

        final QBean<WorkingDayDto> bean = Projections.fields(WorkingDayDto.class,
                entity.workDate,
                entity.weekday,
                entity.workday);

        BooleanExpression condition = entity.country.eq(vo.getCountry())
                .and(entity.workDate.goe(firstDayOfYear))
                .and(entity.workDate.loe(lastDayOfYear));

        if (Objects.nonNull(vo.getWorkday())) {
            condition = condition.and(entity.workday.eq(vo.getWorkday()));
        }

        return jpaQueryFactory.select(bean).from(entity)
                .where(condition)
                .orderBy(entity.id.asc())
                .fetch();
    }

}
