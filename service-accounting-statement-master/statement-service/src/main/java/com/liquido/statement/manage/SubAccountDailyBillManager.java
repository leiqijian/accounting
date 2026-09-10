package com.liquido.statement.manage;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.liquido.base.enums.BusinessTypeEnum;
import com.liquido.base.enums.FeeGroupEnum;
import com.liquido.base.enums.HoldStatusEnum;
import com.liquido.base.enums.OperateSourceEnum;
import com.liquido.base.enums.SettleStatusEnum;
import com.liquido.base.enums.TradingModelEnum;
import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.core.common.snowflake.SnowflakeIdUtil;
import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.core.common.utils.LocalDateUtil;
import com.liquido.statement.exception.StatementExceptionCode;
import com.liquido.statement.pojo.bo.AccountDailyInitBo;
import com.liquido.statement.pojo.bo.DailyExtractableSubAmountInfo;
import com.liquido.statement.pojo.bo.ExtractableAmountBo;
import com.liquido.statement.pojo.bo.RunSubAccountDailyBo;
import com.liquido.statement.pojo.bo.SubAccountDailyBillFactor;
import com.liquido.statement.pojo.bo.SubDailyTransactionBizBo;
import com.liquido.statement.pojo.bo.SubDailyTransactionFeeBo;
import com.liquido.statement.pojo.bo.SubDailyTransactionMoneyBo;
import com.liquido.statement.pojo.dto.AccountDto;
import com.liquido.statement.pojo.dto.AccountStatementDto;
import com.liquido.statement.pojo.entity.QAccountStatement;
import com.liquido.statement.pojo.entity.QAccountStatementBiz;
import com.liquido.statement.pojo.entity.QSubAccountDailyBill;
import com.liquido.statement.pojo.entity.QTransactionBiz;
import com.liquido.statement.pojo.entity.QTransactionFee;
import com.liquido.statement.pojo.entity.QTransactionMoney;
import com.liquido.statement.pojo.entity.SubAccount;
import com.liquido.statement.pojo.entity.SubAccountDailyBill;
import com.liquido.statement.pojo.entity.SubAccountStatement;
import com.liquido.statement.pojo.mapper.ModelMapper;
import com.liquido.statement.pojo.vo.ListAccountStatementVo;
import com.liquido.statement.repository.BatchOperatorRepository;
import com.liquido.statement.repository.SubAccountDailyBillRepository;
import com.liquido.statement.repository.SubAccountRepository;
import com.liquido.statement.service.AccountDailyInitService;
import com.liquido.statement.service.AccountService;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubAccountDailyBillManager {

    private final ModelMapper modelMapper;
    private final AccountService accountService;
    private final JPAQueryFactory jpaQueryFactory;
    private final SubAccountRepository subAccountRepository;
    private final BatchOperatorRepository batchOperatorRepository;
    private final AccountDailyInitService accountDailyInitService;
    private final SubAccountDailyBillRepository subAccountDailyRepository;

    public List<AccountStatementDto> loadMainAccountStatementList(
            final SubAccount subAccount,
            final LocalDate billDate) {

        final List<Long> accountIds = subAccount.getAccountIds();
        final List<AccountDailyInitBo> billList =
                accountDailyInitService.queryDailyBillInitList(accountIds, billDate, billDate);
        final Set<Long> billIds = billList.stream().map(AccountDailyInitBo::getBillId)
                .collect(Collectors.toSet());

        return this.listAccountStatement(ListAccountStatementVo.builder()
                .accountIds(accountIds)
                .billIds(billIds)
                .subMerchantId(subAccount.getSubMerchantId())
                .build());
    }

    public List<SubAccountStatement> buildSubAccountStatement(
            final SubAccount subAccount,
            final List<AccountStatementDto> accountStatementList) {

        final ArrayList<SubAccountStatement> list = new ArrayList<>();

        accountStatementList.sort(Comparator.comparing(AccountStatementDto::getId));

        // build subAccount statement data and add subAccount balance
        BigDecimal tmpBalance = subAccount.getBalance();
        for (final AccountStatementDto dto : accountStatementList) {
            dto.setStartBalance(tmpBalance);
            tmpBalance = tmpBalance.add(dto.getAmount()
                    .multiply(dto.getAmountPon().getCode())
                    .setScale(0, RoundingMode.HALF_UP));
            dto.setEndBalance(tmpBalance);

            final SubAccountStatement subAccountStatement =
                    modelMapper.convertEntity(dto, subAccount);
            subAccountStatement.setId(SnowflakeIdUtil.generate());
            subAccountStatement.setCreatedTime(LocalDateTimeUtil.nowUtc());
            subAccountStatement.setUpdatedTime(LocalDateTimeUtil.nowUtc());
            list.add(subAccountStatement);
        }

        //subAccountStatement data sort by account statement save into db
        return list.stream()
                .sorted(Comparator.comparing(SubAccountStatement::getAccountStatementId))
                .collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Throwable.class)
    public void executeSubAccountCutoff(final RunSubAccountDailyBo bo) {
        try {

            final SubAccountDailyBillFactor factor = this.statisticSubAccountDailyBill(bo);

            subAccountRepository.saveAndFlush(factor.getSubAccount());

            batchOperatorRepository.batchInsertSubAccountStatement(
                    factor.getSubAccountStatementList());

            subAccountDailyRepository.saveAndFlush(this.buildSubAccountDailyBill(factor));

        } catch (Exception e) {
            log.error("run subAccountDailyBill error,e:", e);
            throw StatementExceptionCode.ACCOUNT_DAILY_CUT_FAILED.exception();
        }
    }

    private SubAccountDailyBill buildSubAccountDailyBill(
            final SubAccountDailyBillFactor billFactor) {

        final BigDecimal startBalance =
                Optional.ofNullable(billFactor.getLastSubAccountDailyBill())
                        .map(SubAccountDailyBill::getEndBalance)
                        .orElse(BigDecimal.ZERO);

        final BigDecimal startExtractableBalance =
                Optional.ofNullable(billFactor.getLastSubAccountDailyBill())
                        .map(SubAccountDailyBill::getEndExtractableBalance)
                        .orElse(BigDecimal.ZERO);

        return this.buildSubAccountDailyBill(billFactor, startBalance, startExtractableBalance);
    }

    private SubAccountDailyBillFactor statisticSubAccountDailyBill(
            final RunSubAccountDailyBo bo) {

        final LocalDate billDate = bo.getBillDate();
        final List<SubAccountStatement> subAccountStatements = bo.getSubAccountStatements();
        final SubAccount subAccount = bo.getSubAccount();
        final List<Long> accountIds = subAccount.getAccountIds();

        final BigDecimal netSettlementAmount = subAccountStatements.stream()
                .map(x -> x.getAmount().multiply(x.getAmountPon().getCode()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        subAccount.setBalance(subAccount.getBalance().add(netSettlementAmount));

        // payin & payout accounts
        final List<AccountDto> accountList =
                modelMapper.convert(accountService.findByIds(accountIds));

        // payin + payout last daily bill and current daily bill
        final List<AccountDailyInitBo> dailyBillList =
                accountDailyInitService.queryDailyBillInitList(accountIds, billDate, billDate);

        // lastDaily bill
        final Set<Long> lastDailyBillIds = dailyBillList.stream()
                .map(AccountDailyInitBo::getBillId)
                .collect(Collectors.toSet());

        final List<SubDailyTransactionMoneyBo> transactionMoneyList = statisticsTransactionMoney(
                subAccount.getSubMerchantId(), accountIds, lastDailyBillIds);

        final List<SubDailyTransactionFeeBo> transactionFeeList = statisticsTransactionFees(
                subAccount.getSubMerchantId(), accountIds, lastDailyBillIds);

        final List<SubDailyTransactionBizBo> transactionBizList = statisticsTransactionBiz(
                subAccount.getSubMerchantId(), accountIds, lastDailyBillIds);

        final BigDecimal latestDailyExtractableAmount = this.statisticsDailyExtractableAmount(
                subAccount.getSubMerchantId(), billDate, accountList, lastDailyBillIds,
                subAccountStatements);

        subAccount.setExtractableBalance(
                subAccount.getExtractableBalance().add(latestDailyExtractableAmount));

        // load transaction biz data from transaction biz table by accountId and transactionId
        return SubAccountDailyBillFactor.builder()
                .billDate(billDate)
                .subAccount(subAccount)
                .subAccountStatementList(subAccountStatements)
                .transactionBizList(transactionBizList)
                .transactionMoneyList(transactionMoneyList)
                .transactionFeeList(transactionFeeList)
                .lastSubAccountDailyBill(bo.getLastSubAccountDailyBill())
                .latestDailyExtractableAmount(latestDailyExtractableAmount)
                .build();
    }

    public List<AccountStatementDto> listAccountStatement(
            final ListAccountStatementVo vo) {

        final List<AccountStatementDto> resultData = new ArrayList<>();
        final QAccountStatement entity = QAccountStatement.accountStatement;

        final int batchSize = 1000;
        List<AccountStatementDto> statementList;
        Long maxId = 0L;
        do {
            final BooleanExpression condition = entity.accountId.in(vo.getAccountIds())
                    .and(entity.billId.in(vo.getBillIds()))
                    .and(entity.subMerchantId.eq(vo.getSubMerchantId()))
                    .and(entity.id.gt(maxId));

            statementList = modelMapper.convertAccountStatementList(
                    jpaQueryFactory.selectFrom(entity)
                            .where(condition)
                            .limit(batchSize)
                            .orderBy(entity.id.asc())
                            .fetch());

            maxId = statementList.stream().map(AccountStatementDto::getId).max(Long::compareTo)
                    .orElse(0L);
            resultData.addAll(statementList);
        } while (ObjectUtils.isNotEmpty(statementList) && statementList.size() == batchSize);

        return resultData;
    }

    private List<SubDailyTransactionMoneyBo> statisticsTransactionMoney(
            final String subMerchantId,
            final Collection<Long> accountIds,
            final Collection<Long> billIds) {

        final QTransactionMoney entity = QTransactionMoney.transactionMoney;
        final BooleanExpression condition = entity.accountId.in(accountIds)
                .and(entity.billId.in(billIds))
                .and(entity.subMerchantId.eq(subMerchantId))
                .and(entity.settleStatus.eq(SettleStatusEnum.SUCCESS));

        final QBean<SubDailyTransactionMoneyBo> bean =
                Projections.fields(SubDailyTransactionMoneyBo.class,
                        entity.accountId,
                        entity.transactionTypeCode,
                        entity.id.count().coalesce(0L).as("totalCount"),
                        (entity.settlementAmount.multiply(entity.amountPon)).sum()
                                .coalesce(BigDecimal.ZERO).as("totalSettlementAmount"),
                        entity.settlementCurrency.as("settlementCurrency"));

        return jpaQueryFactory.select(bean)
                .from(entity)
                .where(condition)
                .groupBy(entity.accountId, entity.transactionTypeCode, entity.settlementCurrency)
                .fetch();
    }

    private List<SubDailyTransactionFeeBo> statisticsTransactionFees(
            final String subMerchantId,
            final Collection<Long> accountIds,
            final Collection<Long> billIds) {

        final QTransactionFee entity = QTransactionFee.transactionFee;
        final BooleanExpression condition = entity.accountId.in(accountIds)
                .and(entity.billId.in(billIds))
                .and(entity.subMerchantId.eq(subMerchantId))
                .and(entity.settleStatus.eq(SettleStatusEnum.SUCCESS));

        final QBean<SubDailyTransactionFeeBo> bean =
                Projections.fields(SubDailyTransactionFeeBo.class,
                        entity.accountId,
                        entity.transactionTypeCode,
                        entity.feeGroup.as("feeGroup"),
                        (entity.calculateAmount.multiply(entity.amountPon)).sum()
                                .coalesce(BigDecimal.ZERO).as("totalCalculateAmount"),

                        (entity.settlementAmount.multiply(entity.amountPon)).sum()
                                .coalesce(BigDecimal.ZERO).as("totalSettlementAmount"),

                        entity.settlementCurrency.as("settlementCurrency"),
                        entity.id.count().coalesce(0L).as("totalCount"));

        return jpaQueryFactory.select(bean)
                .from(entity)
                .where(condition)
                .groupBy(entity.accountId,
                        entity.transactionTypeCode,
                        entity.settlementCurrency,
                        entity.feeGroup)
                .fetch();
    }

    public List<SubDailyTransactionBizBo> statisticsTransactionBiz(
            final String subMerchantId,
            final Collection<Long> accountIds,
            final Collection<Long> billIds) {

        final QTransactionBiz entity = QTransactionBiz.transactionBiz;
        final BooleanExpression condition = entity.accountId.in(accountIds)
                .and(entity.billId.in(billIds))
                .and(entity.subMerchantId.eq(subMerchantId))
                .and(entity.settlementStatus.eq(SettleStatusEnum.SUCCESS))
                .and(entity.operateSource.eq(OperateSourceEnum.ONLINE));

        final NumberTemplate<BigDecimal> settlementAmount =
                Expressions.numberTemplate(BigDecimal.class, " ({0} * {1}) ",
                        entity.settlementAmount, entity.amountPon);

        final QBean<SubDailyTransactionBizBo> bean =
                Projections.fields(SubDailyTransactionBizBo.class,
                        entity.businessType.as("businessType"),
                        settlementAmount.sum().coalesce(BigDecimal.ZERO).as("totalAmount"),
                        entity.feeAmount.sum().coalesce(BigDecimal.ZERO).as("totalFee"),
                        entity.taxAmount.sum().coalesce(BigDecimal.ZERO).as("totalTax"),
                        entity.id.count().coalesce(0L).as("totalCount"));

        final List<SubDailyTransactionBizBo> dataList = jpaQueryFactory.select(bean)
                .from(entity)
                .where(condition)
                .groupBy(entity.businessType)
                .fetch();

        // set default value if empty
        for (final BusinessTypeEnum type : BusinessTypeEnum.values()) {
            if (dataList.stream().noneMatch(item -> item.getBusinessType() == type)) {
                dataList.add(SubDailyTransactionBizBo.builder()
                        .businessType(type)
                        .totalAmount(BigDecimal.ZERO)
                        .totalFee(BigDecimal.ZERO)
                        .totalTax(BigDecimal.ZERO)
                        .totalCount(0L)
                        .build());
            }
        }

        return dataList;
    }

    private BigDecimal statisticsDailyExtractableAmount(
            final String subMerchantId,
            final LocalDate billDate,
            final List<AccountDto> accountList,
            final Set<Long> latestDailyBillIds,
            final List<SubAccountStatement> subAccountStatements) {

        final List<Long> accountIds = accountList.stream().map(AccountDto::getId)
                .collect(Collectors.toList());

        // payin + payout latest daily biz total amount
        final BigDecimal latestSubAccountBizAmount = this.statisticsDailyAccountStatementBizBill(
                subMerchantId, accountIds, latestDailyBillIds);

        // load payin account info
        final Map<TransactionTypeCodeEnum, AccountDto> accountMap = accountList.stream()
                .filter(x -> x.getTransactionTypeCode() == TransactionTypeCodeEnum.PAY_IN)
                .collect(Collectors.toMap(AccountDto::getTransactionTypeCode, Function.identity()));

        // statistics payin extractable amount
        final DailyExtractableSubAmountInfo latestDailyPayinOccurredAmount =
                ObjectUtils.isNotEmpty(accountMap) ?
                        this.statisticsDailyPayInAccountExtractableAmount(
                                accountMap.get(TransactionTypeCodeEnum.PAY_IN).getId(),
                                subMerchantId,
                                billDate)
                        : DailyExtractableSubAmountInfo.builder()
                        .latestDailyTnExtractableAmount(BigDecimal.ZERO)
                        //.currentDailyTnExtractableAmount(BigDecimal.ZERO)
                        .latestDailyT0ExtractableAmount(BigDecimal.ZERO)
                        //.currentDailyT0ExtractableAmount(BigDecimal.ZERO)
                        .build();

        final BigDecimal latestDailyPayoutOccurredAmount = subAccountStatements.stream()
                .filter(x -> BusinessTypeEnum.TRANSACTION == x.getBusinessType())
                .filter(x -> TransactionTypeCodeEnum.PAY_OUT == x.getTransactionTypeCode())
                .map(x -> x.getAmount().multiply(x.getAmountPon().getCode()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return BigDecimal.ZERO
                /* payin latest daily t0+tn */
                .add(latestDailyPayinOccurredAmount.getLatestDailyT0ExtractableAmount())
                .add(latestDailyPayinOccurredAmount.getLatestDailyTnExtractableAmount())

                /* payout latest daily t0 */
                .add(latestDailyPayoutOccurredAmount)

                /* payin + payout latest daily biz t0 */
                .add(latestSubAccountBizAmount);
    }


    private DailyExtractableSubAmountInfo statisticsDailyPayInAccountExtractableAmount(
            final Long accountId,
            final String subMerchantId,
            final LocalDate billDate) {

        // Statistics yesterday total extractable amount
        final Pair<BigDecimal, BigDecimal> latestDailyPair =
                this.statisticsDailyExtractableAmount(accountId, subMerchantId, billDate);

        // Statistics current day total extractable amount
        /* final Pair<BigDecimal, BigDecimal> curentDailyPair =
                this.statisticsDailyExtractableAmount(accountId, subMerchantId,
                        billDate.plusDays(1));
        */

        return DailyExtractableSubAmountInfo.builder()
                .latestDailyT0ExtractableAmount(latestDailyPair.getLeft())
                .latestDailyTnExtractableAmount(latestDailyPair.getRight())
                //.currentDailyT0ExtractableAmount(curentDailyPair.getLeft())
                //.currentDailyTnExtractableAmount(curentDailyPair.getRight())
                .build();
    }

    // sum latest daily payin + payout extractable amount
    private BigDecimal statisticsDailyAccountStatementBizBill(
            final String subMerchantId,
            final Collection<Long> accountIds,
            final Collection<Long> dailyBillIds) {

        final QAccountStatementBiz entity = QAccountStatementBiz.accountStatementBiz;
        return jpaQueryFactory.select(entity.extractableAmount.sum()
                        .coalesce(BigDecimal.ZERO).as("totalAmount"))
                .from(entity)
                .where(entity.accountId.in(accountIds)
                        .and(entity.billId.in(dailyBillIds))
                        .and(entity.subMerchantId.eq(subMerchantId)))
                .fetchOne();
    }

    /**
     * Statistics Daily Extractable Amount
     *
     * @return Pair.of(dailyT0ExtractableAmount, dailyTnExtractableAmount)
     */
    private Pair<BigDecimal, BigDecimal> statisticsDailyExtractableAmount(
            final Long accountId,
            final String subMerchantId,
            final LocalDate beCreditedDate) {

        final QTransactionMoney entity = QTransactionMoney.transactionMoney;
        final BooleanExpression condition = entity.accountId.eq(accountId)
                .and(entity.beCreditedDate.eq(beCreditedDate))
                .and(entity.holdStatus.in(HoldStatusEnum.NORMAL, HoldStatusEnum.UNHOLD))
                .and(entity.subMerchantId.eq(subMerchantId));

        final List<ExtractableAmountBo> dailyTransactionList =
                jpaQueryFactory.select(Projections.fields(ExtractableAmountBo.class,
                                entity.tradingModel,
                                entity.beCreditedAmount.sum()
                                        .coalesce(BigDecimal.ZERO).as("totalAmount")))
                        .from(entity)
                        .where(condition)
                        .groupBy(entity.tradingModel)
                        .fetch();

        /* statistics T0 total extractable amount */
        final BigDecimal dailyT0ExtractableAmount = dailyTransactionList.stream()
                .filter(x -> TradingModelEnum.INSTANT_TRADING.contains(x.getTradingModel()))
                .map(ExtractableAmountBo::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        /* statistics Tn total extractable amount */
        final BigDecimal dailyTnExtractableAmount = dailyTransactionList.stream()
                .filter(x -> !TradingModelEnum.INSTANT_TRADING.contains(x.getTradingModel()))
                .map(ExtractableAmountBo::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return Pair.of(dailyT0ExtractableAmount, dailyTnExtractableAmount);
    }

    public SubAccountDailyBill loadLastSubAccountDailyBill(
            final Long subAccountId) {

        final QSubAccountDailyBill subAccountDailyBill = QSubAccountDailyBill.subAccountDailyBill;
        return jpaQueryFactory.selectFrom(subAccountDailyBill)
                .where(subAccountDailyBill.subAccountId.eq(subAccountId))
                .orderBy(subAccountDailyBill.billDate.desc())
                .limit(1)
                .fetchOne();
    }

    private SubAccountDailyBill buildSubAccountDailyBill(
            final SubAccountDailyBillFactor billFactor,
            final BigDecimal startBalance,
            final BigDecimal startExtractableBalance) {

        final LocalDate billDate = billFactor.getBillDate();
        final SubAccount subAccount = billFactor.getSubAccount();

        final List<SubAccountStatement> subAccountStatementList =
                billFactor.getSubAccountStatementList();

        final List<SubDailyTransactionMoneyBo> transactionMoney =
                billFactor.getTransactionMoneyList();

        final List<SubDailyTransactionFeeBo> transactionFee =
                billFactor.getTransactionFeeList();

        final List<SubDailyTransactionBizBo> transactionBizMap =
                billFactor.getTransactionBizList();

        // payin
        final long payinTransactionCount = transactionMoney.stream()
                .filter(x -> TransactionTypeCodeEnum.PAY_IN == x.getTransactionTypeCode())
                .map(SubDailyTransactionMoneyBo::getTotalCount)
                .reduce(0L, Long::sum);

        final BigDecimal payinSettlementAmount = transactionMoney.stream()
                .filter(x -> TransactionTypeCodeEnum.PAY_IN == x.getTransactionTypeCode())
                .map(SubDailyTransactionMoneyBo::getTotalSettlementAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        final BigDecimal payinTransactionFee = transactionFee.stream()
                .filter(x -> TransactionTypeCodeEnum.PAY_IN == x.getTransactionTypeCode())
                .filter(x -> FeeGroupEnum.TRANSACTION_FEE == x.getFeeGroup())
                .map(SubDailyTransactionFeeBo::getTotalSettlementAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        final BigDecimal payinTransactionTax = transactionFee.stream()
                .filter(x -> TransactionTypeCodeEnum.PAY_IN == x.getTransactionTypeCode())
                .filter(x -> FeeGroupEnum.TAX == x.getFeeGroup())
                .map(SubDailyTransactionFeeBo::getTotalSettlementAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // payout
        final long payoutTransactionCount = transactionMoney.stream()
                .filter(x -> TransactionTypeCodeEnum.PAY_OUT == x.getTransactionTypeCode())
                .map(SubDailyTransactionMoneyBo::getTotalCount)
                .reduce(0L, Long::sum);

        final BigDecimal payoutSettlementAmount = transactionMoney.stream()
                .filter(x -> TransactionTypeCodeEnum.PAY_OUT == x.getTransactionTypeCode())
                .map(SubDailyTransactionMoneyBo::getTotalSettlementAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        final BigDecimal payoutTransactionFee = transactionFee.stream()
                .filter(x -> TransactionTypeCodeEnum.PAY_OUT == x.getTransactionTypeCode())
                .filter(x -> FeeGroupEnum.TRANSACTION_FEE == x.getFeeGroup())
                .map(SubDailyTransactionFeeBo::getTotalSettlementAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        final BigDecimal payoutTransactionTax = transactionFee.stream()
                .filter(x -> TransactionTypeCodeEnum.PAY_OUT == x.getTransactionTypeCode())
                .filter(x -> FeeGroupEnum.TAX == x.getFeeGroup())
                .map(SubDailyTransactionFeeBo::getTotalSettlementAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // topup
        final long topupCount = transactionBizMap.stream()
                .filter(x -> BusinessTypeEnum.TOPUP == x.getBusinessType())
                .map(SubDailyTransactionBizBo::getTotalCount)
                .reduce(0L, Long::sum);
        final BigDecimal topupAmount = transactionBizMap.stream()
                .filter(x -> BusinessTypeEnum.TOPUP == x.getBusinessType())
                .map(SubDailyTransactionBizBo::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        final BigDecimal topupFee = transactionBizMap.stream()
                .filter(x -> BusinessTypeEnum.TOPUP == x.getBusinessType())
                .map(SubDailyTransactionBizBo::getTotalFee)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        final BigDecimal topupTax = transactionBizMap.stream()
                .filter(x -> BusinessTypeEnum.TOPUP == x.getBusinessType())
                .map(SubDailyTransactionBizBo::getTotalTax)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // transfer
        final long transferCount = transactionBizMap.stream()
                .filter(x -> BusinessTypeEnum.TRANSFER_OUT == x.getBusinessType())
                .map(SubDailyTransactionBizBo::getTotalCount)
                .reduce(0L, Long::sum);
        final BigDecimal transferAmount = transactionBizMap.stream()
                .filter(x -> BusinessTypeEnum.TRANSFER_OUT == x.getBusinessType())
                .map(SubDailyTransactionBizBo::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        final BigDecimal transferFee = transactionBizMap.stream()
                .filter(x -> BusinessTypeEnum.TRANSFER_OUT == x.getBusinessType())
                .map(SubDailyTransactionBizBo::getTotalFee)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        final BigDecimal transferTax = transactionBizMap.stream()
                .filter(x -> BusinessTypeEnum.TRANSFER_OUT == x.getBusinessType())
                .map(SubDailyTransactionBizBo::getTotalTax)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // exchange
        final long exchangeCount = transactionBizMap.stream()
                .filter(x -> BusinessTypeEnum.EXCHANGE == x.getBusinessType())
                .map(SubDailyTransactionBizBo::getTotalCount)
                .reduce(0L, Long::sum);
        final BigDecimal exchangeAmount = transactionBizMap.stream()
                .filter(x -> BusinessTypeEnum.EXCHANGE == x.getBusinessType())
                .map(SubDailyTransactionBizBo::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        final BigDecimal exchangeFee = transactionBizMap.stream()
                .filter(x -> BusinessTypeEnum.EXCHANGE == x.getBusinessType())
                .map(SubDailyTransactionBizBo::getTotalFee)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        final BigDecimal exchangeTax = transactionBizMap.stream()
                .filter(x -> BusinessTypeEnum.EXCHANGE == x.getBusinessType())
                .map(SubDailyTransactionBizBo::getTotalTax)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        final long adjustmentCount = transactionBizMap.stream()
                .filter(x -> BusinessTypeEnum.ADJUSTMENT == x.getBusinessType())
                .map(SubDailyTransactionBizBo::getTotalCount)
                .reduce(0L, Long::sum);
        final BigDecimal adjustmentAmount = transactionBizMap.stream()
                .filter(x -> BusinessTypeEnum.ADJUSTMENT == x.getBusinessType())
                .map(SubDailyTransactionBizBo::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        final BigDecimal happenAmount = subAccountStatementList.stream()
                .map(x -> x.getAmount().multiply(x.getAmountPon().getCode()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return SubAccountDailyBill.builder()
                .merchantId(subAccount.getMerchantId())
                .countryCode(subAccount.getCountryCode())
                .subMerchantId(subAccount.getSubMerchantId())
                .subAccountId(subAccount.getId())
                .billMonth(Integer.parseInt(billDate.format(LocalDateUtil.FORMAT_YYYYMM)))
                .billDate(billDate)
                .startBalance(startBalance)
                .happenAmount(happenAmount)
                .endBalance(startBalance.add(happenAmount))
                .endExtractableBalance(startExtractableBalance
                        .add(billFactor.getLatestDailyExtractableAmount()))
                // payin
                .payinTransactionCount(payinTransactionCount)
                .payinSettlementAmount(payinSettlementAmount)
                .payinTransactionFee(payinTransactionFee)
                .payinTransactionTax(payinTransactionTax)
                // payout
                .payoutTransactionCount(payoutTransactionCount)
                .payoutSettlementAmount(payoutSettlementAmount)
                .payoutTransactionFee(payoutTransactionFee)
                .payoutTransactionTax(payoutTransactionTax)
                // topup
                .topupCount(topupCount)
                .topupAmount(topupAmount)
                .topupFee(topupFee)
                .topupTax(topupTax)
                // transfer
                .transferCount(transferCount)
                .transferAmount(transferAmount)
                .transferFee(transferFee)
                .transferTax(transferTax)
                //exchange
                .exchangeCount(exchangeCount)
                .exchangeAmount(exchangeAmount)
                .exchangeFee(exchangeFee)
                .exchangeTax(exchangeTax)
                // adjustment
                .adjustmentCount(adjustmentCount)
                .adjustmentAmount(adjustmentAmount)
                .currency(subAccount.getCurrency())
                .createdTime(LocalDateTimeUtil.nowUtc())
                .updatedTime(LocalDateTimeUtil.nowUtc())
                .build();
    }
}
