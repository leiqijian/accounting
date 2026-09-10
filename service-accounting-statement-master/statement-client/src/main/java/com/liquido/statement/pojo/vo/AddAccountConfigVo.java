package com.liquido.statement.pojo.vo;

import java.io.Serializable;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.liquido.statement.pojo.bo.AccountConfigData;
import com.liquido.statement.pojo.bo.ExchangeRateConfig;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddAccountConfigVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull
    private Long accountId;

    @Valid
    private ExchangeRateConfig exchangeRateConfig;

    @Valid
    @NotNull
    private AccountConfigData accountConfigData;

}
