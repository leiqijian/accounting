package com.liquido.statement.pojo.mapper;

import java.util.List;

import com.liquido.base.pojo.dto.PaymentConfigDto;
import com.liquido.statement.pojo.bo.AccountBo;
import com.liquido.statement.pojo.bo.PaymentConfigBo;
import com.liquido.statement.pojo.bo.TransactionInProgressBo;
import com.liquido.statement.pojo.bo.TransactionSummaryBo;
import com.liquido.statement.pojo.dto.AccountConfigDto;
import com.liquido.statement.pojo.dto.AccountDailyBillDto;
import com.liquido.statement.pojo.dto.AccountDto;
import com.liquido.statement.pojo.dto.AccountStatementDto;
import com.liquido.statement.pojo.dto.DailyExchangeRateDto;
import com.liquido.statement.pojo.dto.DealershipWithdrawalInfoDto;
import com.liquido.statement.pojo.dto.GlobalAccountDto;
import com.liquido.statement.pojo.dto.GlobalStatementDto;
import com.liquido.statement.pojo.dto.HourlyExchangeRateDto;
import com.liquido.statement.pojo.dto.ListTransactionSummaryDto;
import com.liquido.statement.pojo.dto.PageAccountDto;
import com.liquido.statement.pojo.dto.RealTimeExchangeRateDto;
import com.liquido.statement.pojo.dto.SubAccountDailyBillDto;
import com.liquido.statement.pojo.dto.SubAccountDto;
import com.liquido.statement.pojo.dto.SummarySubAccountDto;
import com.liquido.statement.pojo.dto.TransactionChargeBackOrderDto;
import com.liquido.statement.pojo.dto.TransactionFeeDto;
import com.liquido.statement.pojo.dto.TransactionInProgressDto;
import com.liquido.statement.pojo.dto.TransactionMoneyDto;
import com.liquido.statement.pojo.entity.Account;
import com.liquido.statement.pojo.entity.AccountBalanceSnapshot;
import com.liquido.statement.pojo.entity.AccountConfig;
import com.liquido.statement.pojo.entity.AccountDailyBill;
import com.liquido.statement.pojo.entity.AccountStatement;
import com.liquido.statement.pojo.entity.BatchWithdrawalApply;
import com.liquido.statement.pojo.entity.DailyExchangeRate;
import com.liquido.statement.pojo.entity.GlobalAccount;
import com.liquido.statement.pojo.entity.GlobalStatement;
import com.liquido.statement.pojo.entity.HourlyExchangeRate;
import com.liquido.statement.pojo.entity.SubAccount;
import com.liquido.statement.pojo.entity.SubAccountDailyBill;
import com.liquido.statement.pojo.entity.SubAccountStatement;
import com.liquido.statement.pojo.entity.TransactionChargeBackOrder;
import com.liquido.statement.pojo.entity.TransactionCost;
import com.liquido.statement.pojo.entity.TransactionFee;
import com.liquido.statement.pojo.entity.TransactionInProgress;
import com.liquido.statement.pojo.entity.TransactionMoney;
import com.liquido.statement.pojo.entity.TransactionSummary;
import com.liquido.statement.pojo.vo.TransactionInProgressVo;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ModelMapper {
    ModelMapper INSTANCE = Mappers.getMapper(ModelMapper.class);

    @Mappings({@Mapping(target = "overWithdrawalAmount", ignore = true),
            @Mapping(target = "accountConfig", source = "entity.accountConfig"),
            @Mapping(target = "unavailableAmount", ignore = true),
            @Mapping(target = "pendingAmount", ignore = true),})
    AccountDto convert(final Account entity);

    @Mappings({@Mapping(target = "merchantCode", ignore = true),
            @Mapping(target = "merchantName", ignore = true),
            @Mapping(target = "exchangeRateCurrency", ignore = true),
            @Mapping(target = "availableAmount", ignore = true),
            @Mapping(target = "pendingAmount", ignore = true),
            @Mapping(target = "unavailableAmount", ignore = true),
            @Mapping(target = "holdingAmount", ignore = true),
            @Mapping(target = "totalBalance", expression = "java(entity.getLatestDailyBalance().add(entity.getSubTotalAmount()))"),
            @Mapping(target = "accountConfigDto", source = "entity.accountConfig")})
    PageAccountDto convertPage(final Account entity);

    AccountConfigDto convert(final AccountConfig entity);

    AccountBo convertBo(final Account entity);

    List<AccountDto> convert(final List<Account> entityList);

    AccountDailyBillDto convertBo(final AccountDailyBill entity);

    List<AccountDailyBillDto> convertDailyBillList(final List<AccountDailyBill> entityList);

    PaymentConfigBo convertBo(final PaymentConfigDto dto);

    List<TransactionSummaryBo> convertList(final List<TransactionSummary> list);

    List<AccountStatementDto> convertAccountStatementList(final List<AccountStatement> list);

    List<TransactionFeeDto> convertFeeList(final List<TransactionFee> list);

    PaymentConfigBo convert(final PaymentConfigDto dto);

    TransactionMoneyDto convertBo(final TransactionMoney entity);

    List<TransactionMoneyDto> convertBo(final List<TransactionMoney> list);

    GlobalAccountDto convert(final GlobalAccount globalAccount);

    @Mapping(ignore = true, target = "subAccountInfo")
    GlobalStatementDto convert(final GlobalStatement globalStatement);

    TransactionCost copy(final TransactionCost cost);

    DailyExchangeRateDto convertBo(final RealTimeExchangeRateDto dto);

    DailyExchangeRateDto convertDto(final DailyExchangeRate dto);

    TransactionChargeBackOrderDto convertTransactionChargeBackOrderDto(
            final TransactionChargeBackOrder order);

    List<ListTransactionSummaryDto> convertTransactionSummaryList(
            final List<TransactionSummary> list);

    @Mappings({
            @Mapping(target = "createdTime", expression = "java(com.liquido.core.common.utils.LocalDateTimeUtil.nowUtc())"),
            @Mapping(target = "updatedTime", ignore = true),
            @Mapping(target = "version", ignore = true),
            @Mapping(target = "delFlag", ignore = true),
            @Mapping(target = "remark", ignore = true),
    })
    TransactionInProgress convert(final TransactionInProgressBo vo);

    TransactionInProgressDto convert(final TransactionInProgress entity);

    @Mapping(target = "isChangedAmount", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "merchantId", ignore = true)
    @Mapping(target = "accountId", ignore = true)
    @Mapping(target = "status", ignore = true)
    TransactionInProgressBo convertBo(final TransactionInProgressVo vo);


    @Mappings({
            @Mapping(target = "createdTime", ignore = true),
            @Mapping(target = "updatedTime", ignore = true),
            @Mapping(target = "createdBy", ignore = true),
            @Mapping(target = "updatedBy", ignore = true),
            @Mapping(target = "version", ignore = true),
            @Mapping(target = "delFlag", ignore = true),
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "accountStatementId", source = "vo.id"),
            @Mapping(target = "subAccountId", source = "subAccount.id"),
            @Mapping(target = "subMerchantId", source = "subAccount.subMerchantId"),
            @Mapping(target = "currency", source = "vo.currency"),
            @Mapping(target = "countryCode", source = "subAccount.countryCode"),
            @Mapping(target = "merchantId", source = "subAccount.merchantId")
    })
    SubAccountStatement convertEntity(final AccountStatementDto vo, final SubAccount subAccount);

    List<HourlyExchangeRateDto> convertDto(final List<HourlyExchangeRate> entity);

    @Mapping(target = "subMerchantName", ignore = true)
    @Mapping(target = "availableBalance", ignore = true)
    @Mapping(target = "unavailableBalance", ignore = true)
    @Mapping(target = "pendingBalance", ignore = true)
    @Mapping(target = "holdBalance", ignore = true)
    SubAccountDto convert(final SubAccount subAccount);

    SubAccountDailyBillDto convert(final SubAccountDailyBill subAccountDailyBill);

    List<DealershipWithdrawalInfoDto> convertWithdrawalApplyList(
            final List<BatchWithdrawalApply> applyList);

    @Mapping(target = "remark", ignore = true)
    DealershipWithdrawalInfoDto convert(final BatchWithdrawalApply applyList);

    @Mapping(target = "date", ignore = true)
    SummarySubAccountDto convertSubAccountDto(final SubAccountDto subAccountDto);

    @Mappings({
            @Mapping(target = "createdTime", ignore = true),
            @Mapping(target = "updatedTime", ignore = true),
            @Mapping(target = "createdBy", ignore = true),
            @Mapping(target = "updatedBy", ignore = true),
            @Mapping(target = "version", ignore = true),
            @Mapping(target = "delFlag", ignore = true),
            @Mapping(target = "remark", ignore = true),
            @Mapping(target = "riskReserveHoldAmount", ignore = true),
            @Mapping(target = "legalHoldAmount", ignore = true),
            @Mapping(target = "accountConfig", source = "dto.accountConfigDto")
    })
    AccountBalanceSnapshot convertToEntity(final PageAccountDto dto);

    List<AccountBalanceSnapshot> convertToEntity(final List<PageAccountDto> accountList);
}
