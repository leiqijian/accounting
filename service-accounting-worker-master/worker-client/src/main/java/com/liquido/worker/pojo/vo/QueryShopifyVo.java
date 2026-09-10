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
public class QueryShopifyVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * payment id
     */
    private String paymentId;

    private String merchantCode;

}
