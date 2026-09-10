package com.liquido.worker.service.fee;

import java.time.LocalDate;
import java.util.List;

import com.liquido.base.pojo.dto.AccountProductVersionDto;
import com.liquido.worker.events.CompareMonthConfigEvent;
import com.liquido.worker.events.FeeMonthConfigGenerateEvent;
import com.liquido.worker.pojo.vo.QueryMerchantMonthVo;

public interface MonthFeeConfigGenerationService {

    List<AccountProductVersionDto> initProductVersion();

    List<AccountProductVersionDto> initAllProductVersion();

    Integer generateMerchantMonthFeeConfig(final QueryMerchantMonthVo vo);

    void generateMonthFeeConfigEvent(final FeeMonthConfigGenerateEvent event);

    void compareMonthConfigDifferenceAndLastMonth(final CompareMonthConfigEvent event);

    void checkMonthFeeConfig();

    Boolean checkMonthFeeConfig(final String timezone, final LocalDate activeDate);
}
