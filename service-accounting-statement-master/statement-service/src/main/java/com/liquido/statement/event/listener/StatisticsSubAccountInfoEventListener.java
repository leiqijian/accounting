package com.liquido.statement.event.listener;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.statement.event.DailyCutSuccessEvent;
import com.liquido.statement.manage.SubAccountDailyCutoffManager;
import com.liquido.statement.pojo.bo.DailyCutSuccessBo;
import com.liquido.statement.pojo.dto.AccountDto;
import com.liquido.statement.pojo.entity.SubAccount;
import com.liquido.statement.pojo.vo.ListAccountVo;
import com.liquido.statement.repository.SubAccountRepository;
import com.liquido.statement.service.AccountService;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class StatisticsSubAccountInfoEventListener {

    private final AccountService accountService;
    private final SubAccountRepository subAccountRepository;
    private final SubAccountDailyCutoffManager subAccountDailyCutoffManager;


    @Async
    @TransactionalEventListener(fallbackExecution = true)
    public void StatisticsSubAccountEventListener(final DailyCutSuccessEvent event) {

        try {
            final List<Long> accountIds = event.getEventArgs().getBillList().stream()
                    .map(DailyCutSuccessBo::getAccountId)
                    .distinct().collect(Collectors.toList());

            log.info("start subAccount daily-cut,accountIds size={},time={}", accountIds.size(),
                    LocalDateTimeUtil.nowUtcToLocal("UTC+8"));

            final List<AccountDto> accountDtoList = Lists.partition(accountIds, 200).stream()
                    .flatMap(accountId -> accountService.listAccount(
                            ListAccountVo.builder().ids(accountId).build()).stream())
                    .collect(Collectors.toList());

            final Map<Long, Set<CountryCodeEnum>> merchantCountryMap = accountDtoList.stream()
                    .collect(Collectors.groupingBy(AccountDto::getMerchantId,
                            Collectors.mapping(AccountDto::getCountryCode, Collectors.toSet())));

            final List<SubAccount> subAccountList = merchantCountryMap.entrySet().stream()
                    .flatMap(entry -> subAccountRepository.findByMerchantIdAndCountryCodeIn(
                            entry.getKey(),
                            entry.getValue()).stream())
                    .collect(Collectors.toList());

            if (ObjectUtils.isEmpty(subAccountList)) {
                return;
            }

            final List<LocalDate> localDates = event.getEventArgs().getBillList()
                    .stream().map(DailyCutSuccessBo::getBillDate).distinct()
                    .sorted(LocalDate::compareTo)
                    .collect(Collectors.toList());

            subAccountList.forEach(subAccount -> subAccountDailyCutoffManager
                    .executeSubAccountDailyCutoff(subAccount, new ArrayList<>(localDates)));
        } catch (Exception e) {
            log.error("StatisticsSubAccountEventListener error:", e);
        }
    }
}
