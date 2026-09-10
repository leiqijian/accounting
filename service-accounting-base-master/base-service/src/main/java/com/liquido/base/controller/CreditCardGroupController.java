package com.liquido.base.controller;


import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.liquido.base.api.CreditCardGroupApi;
import com.liquido.base.pojo.dto.CreditCardGroupDto;
import com.liquido.base.pojo.vo.QueryCreditCardGroupVo;
import com.liquido.base.service.CreditCardGroupService;
import com.liquido.core.mvc.dto.ResponseDto;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CreditCardGroupController implements CreditCardGroupApi {

    private final CreditCardGroupService creditCardGroupService;

    @Override
    @PostMapping("/base/credit-card-group/list/all")
    public ResponseDto<List<CreditCardGroupDto>> queryAllCreditCardGroup() {
        return ResponseDto.success(creditCardGroupService.finalAll());
    }

    /**
     * Add CountryProduct
     *
     * @param vo
     * @return database insert id
     */
    @Override
    @PostMapping("/base/credit-card-group/by/group-code")
    public ResponseDto<List<CreditCardGroupDto>> queryByGroupCode(
            @RequestBody @Valid final QueryCreditCardGroupVo vo) {
        return ResponseDto.success(creditCardGroupService.finalByGroupCode(vo.getGroupCode()));
    }


    /**
     * Add CountryProduct
     *
     * @param iinValue
     * @return database insert id
     */
    @Override
    @PostMapping("/base/credit-card-group/by/inn-value/{iinValue}")
    public ResponseDto<CreditCardGroupDto> queryByIinValue(
            @NotNull @PathVariable(value = "iinValue") final Integer iinValue) {
        return ResponseDto.success(creditCardGroupService.finalByIinRange(iinValue));
    }

}
