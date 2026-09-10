package com.liquido.statement.controller;

import java.util.List;
import javax.validation.Valid;

import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.statement.pojo.dto.AccountDailyBillDto;
import com.liquido.statement.pojo.entity.QAccount;
import com.liquido.statement.pojo.entity.QAccountDailyInit;
import com.liquido.statement.pojo.entity.QTransactionMoney;
import com.liquido.statement.pojo.vo.BatchFixDataVo;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FixHistoryDataController {
    private final JPAQueryFactory jpaQueryFactory;

    @Transactional(rollbackFor = Throwable.class)
    @PostMapping("/statement/transaction-money/be-credited-date/fix")
    public ResponseDto<Void> fixBeCreditedDate(@RequestBody @Valid BatchFixDataVo vo) {
        this.fixData(this.loadData(vo));
        return ResponseDto.success();
    }

    private List<AccountDailyBillDto> loadData(final BatchFixDataVo vo) {
        final QAccountDailyInit init = QAccountDailyInit.accountDailyInit;
        final QAccount account = QAccount.account;
        BooleanExpression condition =
                account.transactionTypeCode.eq(TransactionTypeCodeEnum.PAY_OUT)
                        .and(init.transactionDate.goe(vo.getBeginDate()));

        if (CollectionUtils.isNotEmpty(vo.getAccountIds())) {
            condition = condition.and(init.accountId.in(vo.getAccountIds()));
        }

        return jpaQueryFactory.select(Projections.fields(AccountDailyBillDto.class,
                        init.id,
                        init.accountId,
                        init.transactionDate))
                .from(init)
                .leftJoin(account).on(init.accountId.eq(account.id))
                .where(condition)
                .fetch();
    }

    private void fixData(final List<AccountDailyBillDto> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return;
        }

        final QTransactionMoney entity = QTransactionMoney.transactionMoney;
        for (final AccountDailyBillDto data : dataList) {
            long begin = System.currentTimeMillis();
            jpaQueryFactory.update(entity).set(entity.beCreditedDate, data.getBillDate())
                    .where(entity.accountId.eq(data.getAccountId())
                            .and(entity.billId.eq(data.getId()))
                            .and(entity.beCreditedDate.isNull())).execute();
            log.info("end fix-data accountId={}, billId={}, billDate={} ts={}ms",
                    data.getAccountId(), data.getId(), data.getBillDate(),
                    System.currentTimeMillis() - begin);
        }
    }
}

