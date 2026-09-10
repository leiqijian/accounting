package com.liquido.worker.pojo.dto;

import java.io.Serializable;
import javax.persistence.Convert;

import com.liquido.base.enums.VendorCodeEnum;
import com.liquido.worker.pojo.dto.tokenization.DwSyncTokenInfoDto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DwSyncTokenizationDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * token id
     */
    private String token;

    private String tokenMd5;

    /**
     * merchant name: examples cheng_fan
     */
    private String merchantName;

    /**
     * transaction type: card
     */
    private String type;

    @Convert(converter = VendorCodeEnum.Convert.class)
    private VendorCodeEnum vendor;

    private JsonNode vendorInfo;

    private DwSyncTokenInfoDto tokenInfo;

    /**
     * token create timestamp
     */
    private Long createTime;

    /**
     * token update timestamp
     */
    private Long updateTime;

    /**
     * event happen timestamp
     */
    private Long eventTime;

}
