package com.liquido.base.api;


import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.liquido.base.pojo.dto.CreditCardGroupDto;
import com.liquido.base.pojo.vo.QueryCreditCardGroupVo;
import com.liquido.core.mvc.dto.ResponseDto;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface CreditCardGroupApi {

    @PostMapping("/base/credit-card-group/list/all")
    ResponseDto<List<CreditCardGroupDto>> queryAllCreditCardGroup();

    @PostMapping("/base/credit-card-group/by/group-code")
    ResponseDto<List<CreditCardGroupDto>> queryByGroupCode(
            @RequestBody @Valid final QueryCreditCardGroupVo vo);

    @PostMapping("/base/credit-card-group/by/inn-value/{iinValue}")
    ResponseDto<CreditCardGroupDto> queryByIinValue(
            @NotNull @PathVariable("iinValue") final Integer iinValue);

}
