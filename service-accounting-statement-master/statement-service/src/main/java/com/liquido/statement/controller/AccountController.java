package com.liquido.statement.controller;

import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.liquido.base.enums.OperateModeEnum;
import com.liquido.core.common.logger.LoggerSwitch;
import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.core.mvc.vo.PageVo;
import com.liquido.statement.api.AccountApi;
import com.liquido.statement.manage.InternalAccountTransferManager;
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
import com.liquido.statement.service.AccountService;
import com.liquido.statement.service.AccountingScheduleService;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
public class AccountController implements AccountApi {

    private final AccountService accountService;
    private final AccountingScheduleService accountingScheduleService;
    private final InternalAccountTransferManager internalAccountTransferManager;

    @Override
    @PostMapping("/statement/account/batch-add")
    public ResponseDto<List<AccountDto>> batchAddAccount(
            @RequestBody @Valid final BatchAddAccountVo vo
    ) {
        return ResponseDto.success(accountService.batchAddAccount(vo));
    }

    @Override
    @PostMapping("/statement/account/update")
    public ResponseDto<Void> updateAccount(@RequestBody @Valid final EditAccountVo vo) {
        accountService.update(vo.getId(), vo);
        return ResponseDto.success();
    }

    @Override
    @PostMapping("/statement/account-basic/all/query")
    public ResponseDto<List<AccountBasicInfoDto>> queryAllAccountBasicInfo() {
        return ResponseDto.success(accountService.queryAllAccountBasicInfo());
    }

    @Override
    @PostMapping("/statement/account/all/query")
    public ResponseDto<List<AccountDto>> queryAllAccount() {
        return ResponseDto.success(accountService.queryAllAccount());
    }

    @Override
    @PostMapping("/statement/account/list")
    public ResponseDto<List<AccountDto>> listAccount(
            @RequestBody @Valid final ListAccountVo vo
    ) {
        return ResponseDto.success(accountService.listAccount(vo));
    }

    @Override
    @PostMapping("/statement/account/page")
    public ResponseDto<PageVo<PageAccountDto>> pageAccount(
            @RequestBody @Valid final PageAccountVo vo
    ) {
        return ResponseDto.success(accountService.pageAccount(vo));
    }

    @Override
    @PostMapping("/statement/account/info")
    public ResponseDto<AccountDto> getEffectiveAccountInfo(
            @RequestBody @Valid final QueryAccountVo vo
    ) {
        return ResponseDto.success(accountService.getEffectiveAccountInfo(vo));
    }

    /**
     * Query the set of countries where all accounts under the merchant are located
     */
    @Override
    @PostMapping("/statement/account/country-code/{merchantId}/list")
    public ResponseDto<List<CountryDto>> listAccountCountryCode(
            @PathVariable("merchantId") @Valid @NotNull final Long merchantId
    ) {
        return ResponseDto.success(accountService.findAccountCountryCode(merchantId));
    }

    /**
     * Union Unique Primary Key Match
     */
    @Override
    @PostMapping("/statement/unique-account/query")
    public ResponseDto<AccountDto> queryUniqueAccount(
            @RequestBody @Valid final QueryUniqueAccountVo vo
    ) {
        return ResponseDto.success(accountService.queryUniqueAccount(vo));
    }

    @Override
    @PostMapping("/statement/account/info/query")
    public ResponseDto<AccountDto> queryAccountInfo(
            @RequestBody @Valid final QueryAccountInfoVo vo
    ) {
        return ResponseDto.success(accountService.queryByMerchantIdAndAccountId(vo));
    }

    /**
     * Users switch countries to check account balance information
     * for dashboard home page
     *
     * @param vo merchantId+countryCode
     */
    @Override
    @PostMapping("/statement/account-balance-info/list")
    public ResponseDto<List<AccountBalanceDto>> listAccountBalanceInfo(
            @RequestBody @Valid final QueryMerchantCountryVo vo
    ) {
        return ResponseDto.success(accountService.findAccountBalanceInfo(vo));
    }

    @Override
    @PostMapping("/statement/account/balance/list")
    public ResponseDto<List<ListAccountBalanceDto>> listAccountBalance(
            @RequestBody @Valid final ListAccountBalanceVo vo
    ) {
        return ResponseDto.success(accountService.listAccountBalance(vo));
    }

    /**
     * Only the payIn account can query the extractable balance
     *
     * @param vo merchantId+countryCode
     */
    @Override
    @PostMapping("/statement/account/extractable/balance/query")
    public ResponseDto<AccountExtractableBalanceDto> queryAccountExtractableBalance(
            @RequestBody @Valid final QueryAccountExtractableBalanceVo vo
    ) {
        return ResponseDto.success(accountService.queryAccountExtractableBalance(vo));
    }

    /**
     * query account real-time balance
     * for open api
     *
     * @param vo vo
     */
    @Override
    @PostMapping("/statement/account/balance/query")
    public ResponseDto<AccountBalanceDto> queryAccountBalanceInfo(
            @RequestBody @Valid final QueryAccountInfoVo vo
    ) {
        return ResponseDto.success(accountService.queryAccountBalanceInfo(vo));
    }

    /**
     * merchant balance = pay_in account balance +pay_out account balance
     *
     * @param vo merchantId
     */
    @Override
    @PostMapping("/statement/account/merchant/balance/query")
    public ResponseDto<MerchantBalanceDto> queryMerchantBalanceInfo(
            @RequestBody @Valid final QueryMerchantBalanceInfoVo vo
    ) {
        return ResponseDto.success(accountService.queryMerchantBalanceInfo(vo));
    }

    /**
     * The daily available balance of the account is automatically collected and auto payout
     */
    @Override
    @PostMapping("/statement/account/funds/auto/collection")
    public ResponseDto<Void> fundsAutoCollection(
            @RequestBody @Valid final FundsAutoCollectionVo vo
    ) {
        // dailyFundsCollectionService.dailyFundsCollection(vo);
        return ResponseDto.success();
    }

    @Override
    @PostMapping("/statement/account/recalculation/switch/set")
    public ResponseDto<PageVo<PageAccountDto>> setRecalculationSwitch(
            @RequestBody @Valid final BillRecalculationVo vo
    ) {
        accountService.setRecalculationSwitch(vo);
        return ResponseDto.success();
    }

    @Override
    @PostMapping("/statement/account/bill/recalculation/switch/{accountId}/get")
    public ResponseDto<RecalculationSwitchDto> getRecalculationSwitch(
            @PathVariable("accountId") @Valid @NotNull final Long accountId
    ) {
        return ResponseDto.success(accountService.getRecalculationSwitch(accountId));
    }

    /**
     * Merchant applies for withdrawal account(PAY-IN、PAY-OUT) withdrawal/exchange apply
     */
    @Override
    @PostMapping("/statement/account/approval/apply")
    public ResponseDto<ApprovalApplyDto> approvalApply(
            @RequestBody @Valid final ApprovalApplyVo vo
    ) {
        return ResponseDto.success(accountService.approvalApply(vo));
    }

    /**
     * Merchant applies for withdrawal account(PAY-IN、PAY-OUT) withdrawal/exchange rollback
     */
    @Override
    @PostMapping("/statement/account/approval/rollback")
    public ResponseDto<Void> approvalRollBack(
            @RequestBody @Valid final ApprovalRollBackVo vo
    ) {
        accountService.approvalRollBack(vo);
        return ResponseDto.success();
    }

    /**
     * Within the same merchant Payin account extractable balance
     * auto transferred to Payout account balance
     */
    @Override
    @PostMapping("/statement/account/internal/transfer")
    public ResponseDto<Void> internalAccountTransfer(
            @RequestBody @Valid final AccountTransferVo vo
    ) {
        vo.setOperateMode(OperateModeEnum.MANUAL);
        internalAccountTransferManager.defaultProcessInternalAccountTransfer(vo);
        return ResponseDto.success();
    }

    @Override
    @PostMapping("/statement/account/transfer-amount/query")
    public ResponseDto<QueryAccountTransferAmountDto> queryAccountTransferAmount(
            @RequestBody @Valid final QueryAccountTransferAmountVo vo
    ) {
        return ResponseDto.success(accountService.queryAccountTransferAmount(vo));
    }

    @Override
    @PostMapping("/statement/account/holding/all/query")
    public ResponseDto<List<AccountDto>> queryAllEnableHoldingAccount() {
        return ResponseDto.success(accountService.queryAllEnableHoldingAccount());
    }

    @LoggerSwitch(response = false)
    @Override
    @PostMapping("/statement/account/upload/trade-data/query")
    public ResponseDto<QueryAccountUploadTradeDataDto> queryAccountUploadTradeData(
            @RequestBody @Valid final QueryAccountUploadTradeDataVo vo
    ) {
        return ResponseDto.success(accountService.queryUploadTradeData(vo));
    }

    @Override
    @PostMapping("/statement/account/merchant/balance/batch-query")
    public ResponseDto<List<ListAccountBalanceDto>> batchQueryAccountBalanceByMerchant(
            @RequestBody @Valid final BatchQueryAccountBalanceVo vo
    ) {
        return ResponseDto.success(accountService.listAccountBalance(vo));
    }

    @Override
    @PostMapping("/statement/account/info/subtract/in-progress-amount/query")
    public ResponseDto<AccountDto> querySubtractInProgressAmountAccountInfo(
            @RequestBody @Valid final QuerySubtractInProgressAmountAccountVo vo
    ) {
        return ResponseDto.success(accountService.querySubtractInProgressAmountAccountInfo(vo));
    }

    @Override
    @PostMapping("/statement/account/subtract/in-progress-amount/list")
    public ResponseDto<List<AccountDto>> listSubtractInProgressAmountAccountInfo(
            @RequestBody @Valid final ListAccountVo vo
    ) {
        return ResponseDto.success(accountService.listSubtractInProgressAmountAccountInfo(vo));
    }

    @Override
    @PostMapping("/statement/account/accounting/schedule/calendar")
    public ResponseDto<List<AccountingCalendarDto>> queryAccountingCalendarList(
            @RequestBody @Valid final QueryAccountingCalendarVo vo
    ) {

        return ResponseDto.success(accountingScheduleService.queryAccountingCalendarList(vo));
    }

    @PostMapping("/statement/account/merchant-accounts/query")
    public ResponseDto<List<MerchantAccountsDto>> queryMerchantAccounts() {
        return ResponseDto.success(accountService.queryMerchantAccounts());
    }
}
