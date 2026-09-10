package com.liquido.statement.manage;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.liquido.core.common.exception.CommonExceptionCode;
import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.statement.exception.StatementExceptionCode;
import com.liquido.statement.pojo.bo.RunSubAccountDailyBo;
import com.liquido.statement.pojo.dto.AccountStatementDto;
import com.liquido.statement.pojo.entity.SubAccount;
import com.liquido.statement.pojo.entity.SubAccountDailyBill;
import com.liquido.statement.pojo.entity.SubAccountStatement;
import com.liquido.statement.repository.SubAccountRepository;
import com.liquido.statement.service.monitor.lark.LarkRobotMonitor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubAccountDailyCutoffManager {

    private final LarkRobotMonitor larkRobotMonitor;
    private final SubAccountRepository subAccountRepository;
    private final SubAccountDailyBillManager subAccountDailyBillManager;

    @Async("subAccountDailyCutTaskExecutor")
    public void executeSubAccountDailyCutoff(
            SubAccount subAccount,
            final List<LocalDate> cutoffDateList) {

        if (Objects.isNull(subAccount)
                || CollectionUtils.isEmpty(subAccount.getAccountIds())
                || CollectionUtils.isEmpty((cutoffDateList))) {
            return;
        }

        Collections.sort(cutoffDateList);
        log.info("run SubAccount daily-cut subAccount id={}, cutoffDateList={}",
                subAccount.getId(), cutoffDateList);
        try {
            for (final LocalDate billDate : cutoffDateList) {
                final LocalDate nowDate = LocalDateTimeUtil.nowUtcZonedDateTime()
                        .withZoneSameInstant(ZoneId.of(subAccount.getTimezone()))
                        .toLocalDate();
                if (billDate.equals(nowDate) || billDate.isAfter(nowDate)) {
                    continue;
                }

                // check current subAccount is complete daily-cut
                final SubAccountDailyBill lastSubAccountDailyBill = subAccountDailyBillManager
                        .loadLastSubAccountDailyBill(subAccount.getId());

                if (Objects.nonNull(lastSubAccountDailyBill)
                        && (!(lastSubAccountDailyBill.getBillDate().isBefore(billDate)))) {
                    log.error("Current subAccount is complete daily cut-off,"
                            + "subAccountId={}, billDate={}", subAccount.getId(), billDate);
                    continue;
                }

                // step1: load payin + payout account statements
                final List<AccountStatementDto> mainAccountStatements = subAccountDailyBillManager
                        .loadMainAccountStatementList(subAccount, billDate);

                // step2: build subAccount statement
                final List<SubAccountStatement> subAccountStatements = subAccountDailyBillManager
                        .buildSubAccountStatement(subAccount, mainAccountStatements);

                // step3: handle subAccount daily-bill
                subAccountDailyBillManager.executeSubAccountCutoff(RunSubAccountDailyBo.builder()
                        .subAccount(subAccount)
                        .subAccountStatements(subAccountStatements)
                        .billDate(billDate)
                        .lastSubAccountDailyBill(lastSubAccountDailyBill).build());

                subAccount = subAccountRepository.findById(subAccount.getId())
                        .orElseThrow(CommonExceptionCode.DATA_NOT_FOUND::exception);
            }
        } catch (Exception e) {
            log.error("SubAccount daily cut operation failed", e);
            final String msgContent = String.format("SubAccount Daily-Cut Fail\\n\\n"
                            + "**Merchant:** %s\\n"
                            + "**SubMerchant:** %s\\n"
                            + "**Country:** %s\\n",
                    subAccount.getMerchantId(),
                    subAccount.getSubMerchantId(),
                    subAccount.getCountryCode().getCode());

            larkRobotMonitor.error("SubAccount Daily-Cut Error", msgContent, e.getMessage());

            throw StatementExceptionCode.SUB_ACCOUNT_DAILY_CUT_FAIL.exception(e);
        }
    }
}
