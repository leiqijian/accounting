package com.liquido.worker.events;

import java.util.List;

import com.liquido.base.pojo.dto.MonthlyFeeConfigurationDto;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class CompareMonthConfigEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;

    private final List<MonthlyFeeConfigurationDto> monthlyFeeConfigDtoList;

    public CompareMonthConfigEvent(final List<MonthlyFeeConfigurationDto> monthlyFeeConfigDtoList) {
        super(monthlyFeeConfigDtoList);
        this.monthlyFeeConfigDtoList = monthlyFeeConfigDtoList;
    }
}
