package com.liquido.statement.service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import com.liquido.base.enums.BusinessTypeEnum;
import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.core.mvc.vo.PageVo;
import com.liquido.statement.pojo.bo.AccountBo;
import com.liquido.statement.pojo.bo.DailyBillSummaryBo;
import com.liquido.statement.pojo.bo.DailyCutAccountBo;
import com.liquido.statement.pojo.dto.AccountBalanceDto;
import com.liquido.statement.pojo.dto.AccountBasicInfoDto;
import com.liquido.statement.pojo.dto.AccountDto;
import com.liquido.statement.pojo.dto.AccountExtractableBalanceDto;
import com.liquido.statement.pojo.dto.ApprovalApplyDto;
import com.liquido.statement.pojo.dto.CountryDto;
import com.liquido.statement.pojo.dto.ListAccountBalanceDto;
import com.liquido.statement.pojo.dto.MerchantAccountsDto;
import com.liquido.statement.pojo.dto.MerchantBalanceDto;
import com.liquido.statement.pojo.dto.PageAccountDto;
import com.liquido.statement.pojo.dto.QueryAccountTransferAmountDto;
import com.liquido.statement.pojo.dto.QueryAccountUploadTradeDataDto;
import com.liquido.statement.pojo.dto.RecalculationSwitchDto;
import com.liquido.statement.pojo.entity.Account;
import com.liquido.statement.pojo.entity.TransactionBiz;
import com.liquido.statement.pojo.vo.ApprovalApplyVo;
import com.liquido.statement.pojo.vo.ApprovalBatchApplyVo;
import com.liquido.statement.pojo.vo.ApprovalRollBackVo;
import com.liquido.statement.pojo.vo.BatchAddAccountVo;
import com.liquido.statement.pojo.vo.BatchQueryAccountBalanceVo;
import com.liquido.statement.pojo.vo.BillRecalculationVo;
import com.liquido.statement.pojo.vo.EditAccountVo;
import com.liquido.statement.pojo.vo.ListAccountBalanceVo;
import com.liquido.statement.pojo.vo.ListAccountVo;
import com.liquido.statement.pojo.vo.PageAccountVo;
import com.liquido.statement.pojo.vo.QueryAccountExtractableBalanceVo;
import com.liquido.statement.pojo.vo.QueryAccountInfoVo;
import com.liquido.statement.pojo.vo.QueryAccountTransferAmountVo;
import com.liquido.statement.pojo.vo.QueryAccountUploadTradeDataVo;
import com.liquido.statement.pojo.vo.QueryAccountVo;
import com.liquido.statement.pojo.vo.QueryMerchantBalanceInfoVo;
import com.liquido.statement.pojo.vo.QueryMerchantCountryVo;
import com.liquido.statement.pojo.vo.QuerySubtractInProgressAmountAccountVo;
import com.liquido.statement.pojo.vo.QueryUniqueAccountVo;

public interface AccountService {

    List<AccountDto> batchAddAccount(final BatchAddAccountVo vo);

    void update(final Long id, final EditAccountVo vo);

    List<AccountBasicInfoDto> queryAllAccountBasicInfo();

    List<AccountDto> queryAllAccount();

    AccountDto queryAccountByIdentifier(final String accountIdentifier,
                                        final String merchantCode,
                                        final CountryCodeEnum countryCode,
                                        final TransactionTypeCodeEnum transactionTypeCode);

    List<AccountDto> listAccount(final ListAccountVo vo);

    void saveBalanceSnapshot();

    PageVo<PageAccountDto> pageAccount(final PageAccountVo vo);

    AccountDto getEffectiveAccountInfo(QueryAccountVo vo);

    AccountBo findById(final Long id);

    /**
     * Query account information, even if the account has been logically deleted
     *
     * @param accountId
     *
     * @return
     */
    AccountBo findAccountInfoById(final Long accountId);

    Account getById(final Long id);

    List<Account> findByIds(final Collection<Long> ids);


    List<DailyCutAccountBo> findAllAccountForDailyCut();

    Account findByMerchantIdAndAccountId(final QueryAccountInfoVo vo);

    AccountDto queryByMerchantIdAndAccountId(final QueryAccountInfoVo vo);

    List<CountryDto> findAccountCountryCode(final Long merchantId);

    AccountDto queryUniqueAccount(final QueryUniqueAccountVo vo);

    boolean increaseAccountBalance(final Account account,
                                   final BigDecimal occurredAmount,
                                   final BigDecimal extractableAmount,
                                   final int totalCount);

    /**
     * Scene: use for manual transfer out or internal account transfer;
     * No freezing operation during payment transfer
     *
     * @param account
     * @param occurredAmount
     * @param totalCount
     *
     * @return boolean result
     */
    boolean reduceAccountBalance(final Account account,
                                 final BigDecimal occurredAmount,
                                 final int totalCount);


    /**
     * Scene: use for cancel holding balance
     *
     * @param account
     * @param amount
     *
     * @return boolean result
     */
    boolean increaseExtractableBalance(final Account account,
                                       final BigDecimal amount);

    /**
     * frozen extractable balance
     * see: AccountService#unfrozeExtractableBalance(Account, BigDecimal)
     *
     * @param account
     * @param type
     * @param amount
     *
     * @return boolean result
     */
    boolean frozenExtractableBalance(final Account account,
                                     final BusinessTypeEnum type,
                                     final BigDecimal amount);

    /**
     * unfrozen extractable balance
     * A pair of operations @see: AccountService#frozenExtractableBalance(Account, BigDecimal)
     *
     * @param type
     * @param account
     * @param amount
     *
     * @return boolean result
     */
    boolean unfrozeExtractableBalance(final BusinessTypeEnum type,
                                      final Account account,
                                      final BigDecimal amount);

    /**
     * scene: use for dashboard/admin approval pass post process(reduce frozen amount、PAY-IN)
     *
     * @param type
     * @param account
     * @param amount
     *
     * @return boolean result
     */
    boolean reduceFrozenAmount(final BusinessTypeEnum type,
                               final Account account,
                               final BigDecimal amount);

    boolean unfrozenAmount(final Account account, final BigDecimal unFrozenAmount,
                           final Integer approvedCount,
                           final BigDecimal approvedAmount,
                           final BigDecimal rejectedAmount);

    boolean processAccountDailyCut(final DailyBillSummaryBo summary);

    List<AccountBalanceDto> findAccountBalanceInfo(final QueryMerchantCountryVo vo);

    /**
     * try to lock account
     */
    Account getAccountLocked(final Long accountId, final String lockVal);

    /**
     * try to lock account
     */
    void tryLockAccount(final Long accountId, final String lockVal);

    /**
     * after account settlement unlock account
     *
     * @param accountId
     * @param lockVal
     *
     * @return boolean result
     */
    boolean releaseAccountLock(final Long accountId, final String lockVal);

    void setRecalculationSwitch(final BillRecalculationVo vo);

    RecalculationSwitchDto getRecalculationSwitch(final Long accountId);

    /**
     * for open-api
     *
     * @param vo
     *
     * @return
     */
    AccountBalanceDto queryAccountBalanceInfo(final QueryAccountInfoVo vo);

    /**
     * Merchant applies for withdrawal account(PAY-IN、PAY-OUT) withdrawal/exchange apply
     *
     * @param vo
     */
    ApprovalApplyDto approvalApply(final ApprovalApplyVo vo);

    void batchWithdrawalFrozenAmount(final ApprovalBatchApplyVo vo);

    /**
     * Merchant applies for withdrawal account(PAY-IN、PAY-OUT) withdrawal/exchange rollback
     *
     * @param vo
     */
    void approvalRollBack(final ApprovalRollBackVo vo);

    /**
     * Only the payIn account can query the extractable balance
     *
     * @param vo
     *
     * @return
     */
    AccountExtractableBalanceDto queryAccountExtractableBalance(
            final QueryAccountExtractableBalanceVo vo);


    void reduceAccountBalanceForFundCollection(
            final Account account,
            final TransactionBiz transferOut);

    MerchantBalanceDto queryMerchantBalanceInfo(final QueryMerchantBalanceInfoVo vo);

    List<AccountDto> queryAllEnableHoldingAccount();

    QueryAccountTransferAmountDto queryAccountTransferAmount(final QueryAccountTransferAmountVo vo);

    List<ListAccountBalanceDto> listAccountBalance(final ListAccountBalanceVo vo);

    QueryAccountUploadTradeDataDto queryUploadTradeData(
            final QueryAccountUploadTradeDataVo vo);

    AccountDto querySubtractInProgressAmountAccountInfo(
            final QuerySubtractInProgressAmountAccountVo vo);

    List<AccountDto> listSubtractInProgressAmountAccountInfo(final ListAccountVo vo);

    List<MerchantAccountsDto> queryMerchantAccounts();

    List<ListAccountBalanceDto> listAccountBalance(final BatchQueryAccountBalanceVo vo);

}
