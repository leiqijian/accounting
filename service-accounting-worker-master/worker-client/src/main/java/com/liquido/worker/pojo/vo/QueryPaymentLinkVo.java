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
public class QueryPaymentLinkVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * payment link unique id
     */
    private String linkId;

    /**
     * merchant's order id
     */
    private String merchantReference;

    private String merchantCode;

}
