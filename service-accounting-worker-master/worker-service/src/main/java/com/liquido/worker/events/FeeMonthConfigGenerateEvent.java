package com.liquido.worker.events;

import java.util.List;

import com.liquido.base.pojo.dto.AccountProductVersionDto;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class FeeMonthConfigGenerateEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;

    private final List<AccountProductVersionDto> productVersionVoList;

    private final Boolean autoGenerate;

    private final String timezone;

    public FeeMonthConfigGenerateEvent(final List<AccountProductVersionDto> productVersionVoList,
                                       final Boolean autoGenerate, String timezone) {
        super(productVersionVoList);
        this.productVersionVoList = productVersionVoList;
        this.autoGenerate = autoGenerate;
        this.timezone = timezone;
    }

}
