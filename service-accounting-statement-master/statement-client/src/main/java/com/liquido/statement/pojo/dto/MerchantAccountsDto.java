package com.liquido.statement.pojo.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantAccountsDto implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * merchantId
     */
    private Long merchantId;

    private String merchantCode;

    private Map<String, List<String>> country;

}
