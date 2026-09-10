package com.liquido.statement.service.monitor;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.liquido.base.pojo.dto.MerchantDto;
import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.statement.feign.BaseService;
import com.liquido.statement.pojo.bo.DailyCutMonitorBo;
import com.liquido.statement.pojo.dto.AccountBasicInfoDto;
import com.liquido.statement.pojo.entity.QAccount;
import com.liquido.statement.pojo.entity.QAccountDailyBill;
import com.liquido.statement.service.AccountService;
import com.liquido.statement.service.monitor.lark.LarkRobotMonitor;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service("dailyCutMonitor")
public class DailyCutMonitor implements StatementMonitor {
    private final BaseService baseService;
    private final AccountService accountService;
    private final JPAQueryFactory jpaQueryFactory;

    private final LarkRobotMonitor larkRobotMonitor;

    @Override
    @Async("monitorExecutor")
    public void monitor() {

        final List<DailyCutMonitorBo> warningList = this.queryWarnDailyCutList();
        if (CollectionUtils.isEmpty(warningList)) {
            return;
        }

        warningList.sort(Comparator.comparing(x -> x.getCountryCode().getCode()));

        final StringBuilder line = new StringBuilder();
        final Map<String, String> remarkMap = Maps.newHashMap();
        for (final DailyCutMonitorBo bo : warningList) {
            remarkMap.put(bo.getTimezone(), bo.getExecuteTimeRelativeToUtc8()
                    .format(DateTimeFormatter.ofPattern("MM-dd HH:mm:ss")));
            line.append("**")
                    .append(bo.getBillDate()).append(" | ")
                    .append(bo.getTimezone()).append(" | ")
                    .append(bo.getCountryCode().getCode()).append(" | ")
                    .append(bo.getTransactionTypeCode().getCode()).append(" | ")
                    .append(bo.getMerchantName())
                    .append("**\\n");
        }

        final StringBuilder remark = new StringBuilder();
        for (final Map.Entry<String, String> entry : remarkMap.entrySet()) {
            remark.append(entry.getKey().trim()).append(" | ")
                    .append(entry.getValue())
                    .append("(UTC+8)\\n");
        }

        larkRobotMonitor.error("Abnormal Account For Daily-Cutoff Today", line.toString(),
                remark.toString());
    }

    private List<DailyCutMonitorBo> queryWarnDailyCutList() {
        final List<DailyCutMonitorBo> dataList = this.loadFromDatabase();
        if (CollectionUtils.isEmpty(dataList)) {
            return Collections.emptyList();
        }

        final List<DailyCutMonitorBo> warningBillList = Lists.newArrayList();
        final ZonedDateTime utcZonedDateTime = LocalDateTimeUtil.nowUtcZonedDateTime();
        for (final DailyCutMonitorBo bo : dataList) {
            final LocalDateTime merchantTime =
                    utcZonedDateTime.withZoneSameInstant(ZoneId.of(bo.getTimezone()))
                            .toLocalDateTime();

            // Time of merchant timezone relative to UTC+8
            final LocalDateTime relativeUtc8 =
                    convertTimeZone(LocalDateTime.of(merchantTime.toLocalDate(), LocalTime.MIN),
                            ZoneId.of(bo.getTimezone()), ZoneId.of("UTC+8"));

            bo.setBillDate(merchantTime.toLocalDate().minusDays(1));
            bo.setExecuteTime(LocalDateTime.of(merchantTime.toLocalDate(), LocalTime.MIN)
                    .plusMinutes(15));
            bo.setExecuteTimeRelativeToUtc8(relativeUtc8.plusMinutes(15));

            if (Objects.isNull(bo.getBillId()) && merchantTime.isAfter(bo.getExecuteTime())) {
                final MerchantDto merchant = baseService.getMerchantById(bo.getMerchantId());
                bo.setMerchantName(Objects.nonNull(merchant)
                        ? merchant.getCode() : "-UNKNOWN-");
                warningBillList.add(bo);
            }
        }

        return warningBillList;
    }


    private List<DailyCutMonitorBo> loadFromDatabase() {
        final QAccount account = QAccount.account;
        final QAccountDailyBill bill = QAccountDailyBill.accountDailyBill;

        // Load all account from db
        final List<AccountBasicInfoDto> accountList = accountService.queryAllAccountBasicInfo();
        if (CollectionUtils.isEmpty(accountList)) {
            return Collections.emptyList();
        }

        // Group by timezone
        final Map<String, List<AccountBasicInfoDto>> accountGroup = accountList.stream()
                .collect(Collectors.groupingBy(AccountBasicInfoDto::getTimezone));

        final QBean<DailyCutMonitorBo> bean = Projections.fields(DailyCutMonitorBo.class,
                bill.id.as("billId"),
                account.id.as("accountId"),
                account.merchantId,
                account.countryCode,
                account.transactionTypeCode,
                account.timezone);

        final List<DailyCutMonitorBo> dataList = Lists.newArrayList();
        for (final Map.Entry<String, List<AccountBasicInfoDto>> entry :
                accountGroup.entrySet()) {
            if (StringUtils.isBlank(entry.getKey()) || CollectionUtils.isEmpty(entry.getValue())) {
                continue;
            }

            final LocalDateTime nowDateTime = LocalDateTimeUtil.nowUtcZonedDateTime()
                    .withZoneSameInstant(ZoneId.of(entry.getKey()))
                    .toLocalDateTime();

            // Skip inspection from 0:00 to 0:20
            if (nowDateTime.isBefore(LocalDateTime.of(nowDateTime.toLocalDate(), LocalTime.MIN)
                    .plusMinutes(20))) {
                continue;
            }

            // Skip the account opened today
            final Set<Long> accountIds = entry.getValue().stream()
                    .filter(item -> LocalDateTimeUtil.nowUtc().toLocalDate()
                            .isAfter(item.getCreatedTime().toLocalDate()))
                    .map(AccountBasicInfoDto::getId).collect(Collectors.toSet());

            final List<DailyCutMonitorBo> subList = jpaQueryFactory.select(bean)
                    .from(account)
                    .leftJoin(bill).on(account.id.eq(bill.accountId)
                            .and(bill.billDate.eq(nowDateTime.toLocalDate().minusDays(1))))
                    .where(account.id.in(accountIds).and(bill.id.isNull()))
                    .fetch();

            if (!CollectionUtils.isEmpty(subList)) {
                dataList.addAll(subList);
            }
        }

        return dataList;
    }


    public static LocalDateTime convertTimeZone(final LocalDateTime merchantTime,
                                                final ZoneId merchantTimeZone,
                                                final ZoneId toTimeZone) {
        return merchantTime.atZone(merchantTimeZone)
                .withZoneSameInstant(toTimeZone).toLocalDateTime();
    }
}
