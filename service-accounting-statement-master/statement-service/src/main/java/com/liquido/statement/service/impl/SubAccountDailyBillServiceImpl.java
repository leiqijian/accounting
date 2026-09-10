package com.liquido.statement.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;

import com.liquido.core.common.exception.CommonExceptionCode;
import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.core.mvc.vo.PageVo;
import com.liquido.statement.manage.SubAccountDailyCutoffManager;
import com.liquido.statement.pojo.dto.SubAccountDailyBillDto;
import com.liquido.statement.pojo.dto.SubAccountDto;
import com.liquido.statement.pojo.dto.SummarySubAccountDailyTransactionDto;
import com.liquido.statement.pojo.dto.SummarySubAccountDto;
import com.liquido.statement.pojo.entity.QSubAccountDailyBill;
import com.liquido.statement.pojo.entity.SubAccount;
import com.liquido.statement.pojo.entity.SubAccountDailyBill;
import com.liquido.statement.pojo.mapper.ModelMapper;
import com.liquido.statement.pojo.vo.HandleSubAccountDailyCutVo;
import com.liquido.statement.pojo.vo.QuerySubAccountVo;
import com.liquido.statement.pojo.vo.SummaryDailyTransactionVo;
import com.liquido.statement.repository.SubAccountDailyBillRepository;
import com.liquido.statement.repository.SubAccountRepository;
import com.liquido.statement.service.SubAccountDailyBillService;
import com.liquido.statement.service.SubAccountService;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackOn = Exception.class)
public class SubAccountDailyBillServiceImpl implements SubAccountDailyBillService {

    private final ModelMapper modelMapper;
    private final JPAQueryFactory jpaQueryFactory;
    private final SubAccountService subAccountService;
    private final SubAccountRepository subAccountRepository;
    private final SubAccountDailyCutoffManager subAccountDailyCutoffManager;
    private final SubAccountDailyBillRepository subAccountDailyBillRepository;

    @Override
    public SummarySubAccountDailyTransactionDto summarySubAccountDailyTransaction(
            final SummaryDailyTransactionVo vo) {
        final SubAccountDto subAccountDto = subAccountService.querySubAccount(
                QuerySubAccountVo.builder()
                        .merchantId(vo.getMerchantId())
                        .subMerchantId(vo.getSubMerchantId())
                        .countryCode(vo.getCountryCode())
                        .build());

        final SummarySubAccountDto summarySubAccountDto =
                modelMapper.convertSubAccountDto(subAccountDto);

        summarySubAccountDto.setDate(LocalDateTimeUtil.utcToLocal(LocalDateTimeUtil.nowUtc(),
                subAccountDto.getTimezone()).toLocalDate().minusDays(1L));

        return SummarySubAccountDailyTransactionDto.builder()
                .subAccount(summarySubAccountDto)
                .subAccountDailyBill(pageSubAccountDailyBill(vo))
                .build();
    }

    @Override
    public void handleSubAccountDailyCut(final HandleSubAccountDailyCutVo vo) {
        final LocalDate startDate = vo.getBeginDate();
        final LocalDate endDate = vo.getEndDate();

        if (startDate.isAfter(endDate)
                || !startDate.isBefore(LocalDateTimeUtil.nowUtc().toLocalDate())) {
            throw CommonExceptionCode.PARAMETER_ILLEGAL.exception();
        }

        final List<SubAccount> subAccountList =
                subAccountRepository.findAllById(vo.getSubAccountIdList());
        if (CollectionUtils.isEmpty(subAccountList)) {
            return;
        }

        final List<LocalDate> cutoffDateList = startDate.datesUntil(endDate.plusDays(1))
                .collect(Collectors.toList());

        subAccountList.forEach(subAccount -> subAccountDailyCutoffManager
                .executeSubAccountDailyCutoff(subAccount, new ArrayList<>(cutoffDateList)));

    }

    @Override
    public List<SubAccountDailyBillDto> getLatestSubAccountDailyBillBySubAccountIds(
            final Collection<Long> needReloadSubAccountIds) {

        final QSubAccountDailyBill entity = QSubAccountDailyBill.subAccountDailyBill;
        final QSubAccountDailyBill sub = new QSubAccountDailyBill("sub");

        return jpaQueryFactory.selectFrom(entity)
                .where(
                        entity.subAccountId.in(needReloadSubAccountIds),
                        entity.billDate.in(JPAExpressions.select(sub.billDate.max())
                                .from(sub).where(sub.subAccountId.eq(entity.subAccountId))
                                .groupBy(sub.subAccountId)))
                .fetch()
                .stream()
                .map(modelMapper::convert)
                .collect(Collectors.toList());
    }


    private PageVo<SubAccountDailyBillDto> pageSubAccountDailyBill(
            final SummaryDailyTransactionVo vo) {

        Specification<SubAccountDailyBill> spec = (root, query, cb) -> cb.and(
                cb.equal(root.get("merchantId"), vo.getMerchantId()),
                cb.equal(root.get("subMerchantId"), vo.getSubMerchantId()),
                cb.equal(root.get("countryCode"), vo.getCountryCode()),
                cb.greaterThanOrEqualTo(root.get("billDate"), vo.getStartDate()),
                cb.lessThanOrEqualTo(root.get("billDate"), vo.getEndDate())
        );
        if
        (Boolean.TRUE == vo.getExistTransaction()) {
            spec = spec.and((root, query, cb) ->
                    cb.or(
                            cb.greaterThan(root.get("payinTransactionCount"), 0),
                            cb.greaterThan(root.get("payoutTransactionCount"), 0)));
        }

        final Page<SubAccountDailyBill> page = subAccountDailyBillRepository.findAll(
                spec, PageRequest.of(vo.getPageNo() - 1, vo.getPageSize(),
                        Sort.by(Sort.Direction.DESC, "billDate")));

        if (ObjectUtils.isEmpty(page.getContent())) {
            return PageVo.buildEmptyPage(vo.getPageSize());
        }
        return new PageVo<>(vo.getPageNo(), vo.getPageSize(),
                page.getTotalElements(),
                page.getContent().stream().map(modelMapper::convert).collect(Collectors.toList()));
    }
}
