package com.liquido.base;

import java.util.Map;

import com.liquido.base.api.AccountFeeConfigurationApi;
import com.liquido.base.api.AccountProductApi;
import com.liquido.base.api.AccountProductVersionApi;
import com.liquido.base.api.BizFeeConfigurationApi;
import com.liquido.base.api.CostConfigurationApi;
import com.liquido.base.api.CountryProductApi;
import com.liquido.base.api.CreditCardGroupApi;
import com.liquido.base.api.DictionaryApi;
import com.liquido.base.api.EmailApi;
import com.liquido.base.api.ExtraIncomeConfigurationApi;
import com.liquido.base.api.LarkMessageApi;
import com.liquido.base.api.MerchantApi;
import com.liquido.base.api.MerchantMessageApi;
import com.liquido.base.api.MonitorApi;
import com.liquido.base.api.MonthlyFeeConfigurationApi;
import com.liquido.base.api.PaymentConfigApi;
import com.liquido.base.api.SubMerchantApi;
import com.liquido.base.api.WorkingDayApi;
import com.liquido.base.enums.DictionaryTypeEnum;
import com.liquido.core.common.utils.SpringUtils;

import org.springframework.cloud.openfeign.FeignClient;

public interface BaseApis extends AccountFeeConfigurationApi,
        AccountProductApi, AccountProductVersionApi, BizFeeConfigurationApi, CostConfigurationApi,
        CountryProductApi, CreditCardGroupApi, DictionaryApi, EmailApi, MerchantApi,
        MerchantMessageApi, MonthlyFeeConfigurationApi, PaymentConfigApi, MonitorApi,
        ExtraIncomeConfigurationApi, WorkingDayApi, LarkMessageApi, SubMerchantApi {

    @FeignClient("${base.feign.name:service-accounting-base}")
    interface BaseFeign extends BaseApis {

        default String queryDictValue(final DictionaryTypeEnum dictionaryType,
                                      final String key,
                                      final String... subTypes) {
            return SpringUtils.getBean(BaseApis.BaseFeign.class)
                    .queryDictValue(dictionaryType, String.class, key, subTypes);
        }

        default Map<String, String> queryDictAllValue(final DictionaryTypeEnum dictionaryType,
                                                      final String... subTypes) {
            return SpringUtils.getBean(BaseApis.BaseFeign.class)
                    .queryDictAllValue(dictionaryType, String.class, subTypes);
        }
    }
}
