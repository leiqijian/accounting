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
public class QueryShoplazzaVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String linkId;

    private String orderId;

    private String merchantCode;

}
