package com.liquido.worker.service;

import java.util.List;
import javax.annotation.Resource;

import com.liquido.base.BaseApis;
import com.liquido.base.pojo.dto.MonthlyFeeConfigurationDto;
import com.liquido.base.pojo.vo.QueryMonthFeeConfigVo;
import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.worker.common.AbstractTest;
import com.liquido.worker.events.CompareMonthConfigEvent;

import org.springframework.context.ApplicationEventPublisher;
import org.testng.annotations.Test;

public class MonthlyFeeConfigTest extends AbstractTest {

    @Resource
    private BaseApis.BaseFeign baseFeign;

    @Resource
    private ApplicationEventPublisher publisher;

    @Test(description = "monthlyFeeConfigTest")
    public void testGetMonthFeeConfig() {

        final ResponseDto<List<MonthlyFeeConfigurationDto>> responseDto =
                baseFeign.listMonthFeeConfig(
                        QueryMonthFeeConfigVo.builder()
                                .accountIds(List.of(93869700725939381L, 83864700725799118L))
                                .activeMonth(202408).build());

        publisher.publishEvent(new CompareMonthConfigEvent(responseDto.getData()));
    }
}
