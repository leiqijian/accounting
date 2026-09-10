package com.liquido.worker.pojo.vo;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryExchangeRateAuthVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * client_id
     */
    public String clientId;
    /**
     * client_secret
     */
    private String clientSecret;
    /**
     * grant_type
     */
    private String grantType;

}
