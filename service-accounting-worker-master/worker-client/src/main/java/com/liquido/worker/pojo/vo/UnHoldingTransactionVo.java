package com.liquido.worker.pojo.vo;

import java.io.Serializable;
import java.util.Set;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.liquido.base.enums.CountryCodeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnHoldingTransactionVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    private CountryCodeEnum countryCode;

    @NotBlank
    private String merchantCode;

    @NotNull
    @NotEmpty
    private Set<Long> transactionIds;

}
