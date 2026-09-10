package com.liquido.worker.pojo.bo;

import java.io.Serializable;
import java.util.Map;

import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.base.pojo.dto.AccountProductDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantAccountBo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long merchantId;

    private Long accountId;

    private String merchantCode;

    private CountryCodeEnum countryCode;

    private TransactionTypeCodeEnum transactionTypeCode;

    private Map<String, AccountProductDto> products;

    private String timezone;

}
