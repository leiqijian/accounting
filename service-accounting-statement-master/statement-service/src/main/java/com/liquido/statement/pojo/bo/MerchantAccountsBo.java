package com.liquido.statement.pojo.bo;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantAccountsBo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * merchantId
     */
    private Long merchantId;
    /**
     * accountConfigId
     */
    private String countryCode;

    private List<String> transactionTypeList;

}
