/*
 * @Copyright: 2017 www.yyfax.com Inc. All rights reserved.
 */

package com.liquido.core.mvc.vo;

import java.io.Serializable;

import com.liquido.core.common.utils.JsonUtil;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * request body wrapper
 */
@Data
public class RequestVo<P> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * business parameters object
     */
    private P params;

    @JsonIgnore
    private String userId;

    /**
     * extend filed
     */
    private String extend;

    @Override
    public String toString() {
        return JsonUtil.toJson(this);
    }

}
