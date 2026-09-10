package com.liquido.statement.api;

import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.core.mvc.vo.PageVo;
import com.liquido.statement.pojo.dto.AccountBalanceDto;
import com.liquido.statement.pojo.dto.AccountBasicInfoDto;
import com.liquido.statement.pojo.dto.AccountDto;
import com.liquido.statement.pojo.dto.AccountExtractableBalanceDto;
import com.liquido.statement.pojo.dto.AccountingCalendarDto;
import com.liquido.statement.pojo.dto.ApprovalApplyDto;
import com.liquido.statement.pojo.dto.CountryDto;
import com.liquido.statement.pojo.dto.ListAccountBalanceDto;
import com.liquido.statement.pojo.dto.MerchantAccountsDto;
import com.liquido.statement.pojo.dto.MerchantBalanceDto;
import com.liquido.statement.pojo.dto.PageAccountDto;
import com.liquido.statement.pojo.dto.QueryAccountTransferAmountDto;
import com.liquido.statement.pojo.dto.QueryAccountUploadTradeDataDto;
import com.liquido.statement.pojo.dto.RecalculationSwitchDto;
import com.liquido.statement.pojo.vo.AccountTransferVo;
import com.liquido.statement.pojo.vo.ApprovalApplyVo;
import com.liquido.statement.pojo.vo.ApprovalRollBackVo;
import com.liquido.statement.pojo.vo.BatchAddAccountVo;
import com.liquido.statement.pojo.vo.BatchQueryAccountBalanceVo;
import com.liquido.statement.pojo.vo.BillRecalculationVo;
import com.liquido.statement.pojo.vo.EditAccountVo;
import com.liquido.statement.pojo.vo.FundsAutoCollectionVo;
import com.liquido.statement.pojo.vo.ListAccountBalanceVo;
import com.liquido.statement.pojo.vo.ListAccountVo;
import com.liquido.statement.pojo.vo.PageAccountVo;
import com.liquido.statement.pojo.vo.QueryAccountExtractableBalanceVo;
import com.liquido.statement.pojo.vo.QueryAccountInfoVo;
import com.liquido.statement.pojo.vo.QueryAccountTransferAmountVo;
import com.liquido.statement.pojo.vo.QueryAccountUploadTradeDataVo;
import com.liquido.statement.pojo.vo.QueryAccountVo;
import com.liquido.statement.pojo.vo.QueryAccountingCalendarVo;
import com.liquido.statement.pojo.vo.QueryMerchantBalanceInfoVo;
import com.liquido.statement.pojo.vo.QueryMerchantCountryVo;
import com.liquido.statement.pojo.vo.QuerySubtractInProgressAmountAccountVo;
import com.liquido.statement.pojo.vo.QueryUniqueAccountVo;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface AccountApi {

    @PostMapping("/statement/account/batch-add")
    ResponseDto<List<AccountDto>> batchAddAccount(@RequestBody @Valid final BatchAddAccountVo vo);

    @PostMapping("/statement/account/update")
    ResponseDto<Void> updateAccount(@RequestBody @Valid final EditAccountVo vo);

    @PostMapping("/statement/account-basic/all/query")
    ResponseDto<List<AccountBasicInfoDto>> queryAllAccountBasicInfo();

    @PostMapping("/statement/account/all/query")
    ResponseDto<List<AccountDto>> queryAllAccount();

    @PostMapping("/statement/account/list")
    ResponseDto<List<AccountDto>> listAccount(@RequestBody @Valid final ListAccountVo vo);

    @PostMapping("/statement/account/page")
    ResponseDto<PageVo<PageAccountDto>> pageAccount(
            @RequestBody @Valid final PageAccountVo vo);

    @PostMapping("/statement/account/info")
    ResponseDto<AccountDto> getEffectiveAccountInfo(@RequestBody @Valid final QueryAccountVo vo);

    @PostMapping("/statement/account/country-code/{merchantId}/list")
    ResponseDto<List<CountryDto>> listAccountCountryCode(
            @PathVariable("merchantId") @Valid @NotNull final Long merchantId);

    @PostMapping("/statement/unique-account/query")
    ResponseDto<AccountDto> queryUniqueAccount(@RequestBody @Valid final QueryUniqueAccountVo vo);

    @PostMapping("/statement/account/info/query")
    ResponseDto<AccountDto> queryAccountInfo(@RequestBody @Valid final QueryAccountInfoVo vo);

    @PostMapping("/statement/account-balance-info/list")
    ResponseDto<List<AccountBalanceDto>> listAccountBalanceInfo(
            @RequestBody @Valid final QueryMerchantCountryVo vo);


    @PostMapping("/statement/account/balance/list")
    ResponseDto<List<ListAccountBalanceDto>> listAccountBalance(
            @RequestBody @Valid final ListAccountBalanceVo vo);

    /**
     * Only the payIn account can query the extractable balance
     *
     * @param vo
     * @return
     */
    @PostMapping("/statement/account/extractable/balance/query")
    ResponseDto<AccountExtractableBalanceDto> queryAccountExtractableBalance(
            @RequestBody @Valid final QueryAccountExtractableBalanceVo vo);

    @PostMapping("/statement/account/balance/query")
    ResponseDto<AccountBalanceDto> queryAccountBalanceInfo(
            @RequestBody @Valid final QueryAccountInfoVo vo);

    @PostMapping("/statement/account/merchant/balance/query")
    ResponseDto<MerchantBalanceDto> queryMerchantBalanceInfo(
            @RequestBody @Valid final QueryMerchantBalanceInfoVo vo);

    @PostMapping("/statement/account/funds/auto/collection")
    ResponseDto<Void> fundsAutoCollection(
            @RequestBody @Valid final FundsAutoCollectionVo vo);

    @PostMapping("/statement/account/recalculation/switch/set")
    ResponseDto<PageVo<PageAccountDto>> setRecalculationSwitch(
            @RequestBody @Valid BillRecalculationVo vo);

    @PostMapping("/statement/account/bill/recalculation/switch/{accountId}/get")
    ResponseDto<RecalculationSwitchDto> getRecalculationSwitch(
            @PathVariable("accountId") @Valid @NotNull final Long accountId);

    /**
     * Merchant applies for withdrawal account(PAY-IN) withdrawal apply
     *
     * @return
     */
    @PostMapping("/statement/account/approval/apply")
    ResponseDto<ApprovalApplyDto> approvalApply(@RequestBody @Valid final ApprovalApplyVo vo);

    /**
     * Merchant applies for withdrawal account(PAY-IN) withdrawal rollback
     *
     * @return
     */
    @PostMapping("/statement/account/approval/rollback")
    ResponseDto<Void> approvalRollBack(@RequestBody @Valid final ApprovalRollBackVo vo);

    @PostMapping("/statement/account/internal/transfer")
    ResponseDto<Void> internalAccountTransfer(@RequestBody @Valid final AccountTransferVo vo);


    @PostMapping("/statement/account/transfer-amount/query")
    ResponseDto<QueryAccountTransferAmountDto> queryAccountTransferAmount(
            @RequestBody @Valid final QueryAccountTransferAmountVo vo);

    @PostMapping("/statement/account/holding/all/query")
    ResponseDto<List<AccountDto>> queryAllEnableHoldingAccount();


    @PostMapping("/statement/account/upload/trade-data/query")
    ResponseDto<QueryAccountUploadTradeDataDto> queryAccountUploadTradeData(
            @RequestBody @Valid final QueryAccountUploadTradeDataVo vo);

    @PostMapping("/statement/account/merchant/balance/batch-query")
    ResponseDto<List<ListAccountBalanceDto>> batchQueryAccountBalanceByMerchant(
            @RequestBody @Valid final BatchQueryAccountBalanceVo vo);

    @PostMapping("/statement/account/info/subtract/in-progress-amount/query")
    ResponseDto<AccountDto> querySubtractInProgressAmountAccountInfo(
            @RequestBody @Valid final QuerySubtractInProgressAmountAccountVo vo);

    @PostMapping("/statement/account/subtract/in-progress-amount/list")
    ResponseDto<List<AccountDto>> listSubtractInProgressAmountAccountInfo(
            @RequestBody @Valid final ListAccountVo vo);

    @PostMapping("/statement/account/accounting/schedule/calendar")
    ResponseDto<List<AccountingCalendarDto>> queryAccountingCalendarList(
            @RequestBody @Valid final QueryAccountingCalendarVo vo);

    @PostMapping("/statement/account/merchant-accounts/query")
    ResponseDto<List<MerchantAccountsDto>> queryMerchantAccounts();
}
