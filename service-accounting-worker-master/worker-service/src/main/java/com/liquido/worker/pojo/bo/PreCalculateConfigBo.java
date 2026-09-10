package com.liquido.worker.pojo.bo;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.liquido.base.pojo.dto.AccountProductDto;
import com.liquido.base.pojo.dto.ApmCostConfigurationDto;
import com.liquido.base.pojo.dto.CardCostConfigurationDto;
import com.liquido.base.pojo.dto.ExtraIncomeConfigurationDto;
import com.liquido.base.pojo.dto.MerchantDto;
import com.liquido.base.pojo.dto.MonthlyFeeConfigurationDto;
import com.liquido.base.pojo.dto.WorkingDayDto;
import com.liquido.statement.pojo.dto.AccountDto;
import com.liquido.statement.pojo.dto.DailyExchangeRateDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PreCalculateConfigBo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * merchant info
     */
    private MerchantDto merchantInfo;

    /**
     * merchant account info
     */
    private AccountDto accountInfo;

    /**
     * account product monthly fee configs
     */
    private List<MonthlyFeeConfigurationDto> feeConfigList;

    private List<WorkingDayDto> workdayList;

    private ApmCostConfigurationDto apmCostConfig;

    /**
     * just payin and usd card(credit-card, debit-card) transaction cost fee configs
     */
    private CardCostConfigurationDto cardCostConfig;

    /**
     * extra income configs
     */
    private List<ExtraIncomeConfigurationDto> extraIncomeConfig;

    /**
     * account product configs
     */
    private AccountProductDto accountProduct;

    /**
     * Pair.left (Use for USD -> Account Currency)
     * Pair.right (Use for USD -> Transaction Order Currency)
     */
    Pair<DailyExchangeRateDto, DailyExchangeRateDto> exchangeRate;

    /**
     * transactionDate has been converted to merchant-account timezone;
     */
    private LocalDate transactionDate;

    /**
     * transaction calculationRule(cardType,cardBand,cardRegion,cardUse3ds,cardInstallments)
     */
    private Map<String, String> calculationRule;
}
