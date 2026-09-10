package com.liquido.base.pojo.dto;

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
public class LarkTenantAccessTokenDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer code;

    private Long expire;

    private String msg;

    @JsonProperty("tenant_access_token")
    private String tenantAccessToken;

}
