package com.liquido.base.service;

import java.util.List;

import com.liquido.base.enums.CreditCardGroupCodeEnum;
import com.liquido.base.pojo.dto.CreditCardGroupDto;

public interface CreditCardGroupService {

    List<CreditCardGroupDto> finalAll();

    List<CreditCardGroupDto> finalByGroupCode(final CreditCardGroupCodeEnum groupCode);

    CreditCardGroupDto finalByIinRange(final Integer iinValue);
}
