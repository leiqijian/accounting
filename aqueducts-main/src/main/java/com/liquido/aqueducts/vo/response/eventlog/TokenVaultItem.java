package com.liquido.aqueducts.vo.response.eventlog;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TokenVaultItem {

    private String token;
    private String tokenMd5;
    private String merchantName;
    private String type;
    private String vendor;
    private JsonNode vendorInfo;
    private String id;
    private JsonNode tokenInfo;
    private Long createTime;
    private Long updateTime;
    private Integer eventTime;

}
