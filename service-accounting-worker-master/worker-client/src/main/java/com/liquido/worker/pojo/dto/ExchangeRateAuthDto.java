package com.liquido.worker.pojo.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRateAuthDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * access token
     */
    @JsonProperty("access_token")
    public String accessToken;

    /**
     * token will expire after {expires_in} seconds
     */
    @JsonProperty("expires_in")
    private Integer expiresIn;

    /**
     * Must be "Bearer" here
     */
    @JsonProperty("token_type")
    private String tokenType;

}
