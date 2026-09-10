package com.liquido.core.mvc.vo;

import java.io.Serializable;

import com.liquido.core.common.utils.JsonUtil;

import lombok.Data;

/**
 * private key information
 */
@Data
public class AccessPartnerVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * access key
     */
    private String accessKey;

    /**
     * secret key
     */
    private String secretKey;

    @Override
    public String toString() {
        return JsonUtil.toJson(this);
    }
}
