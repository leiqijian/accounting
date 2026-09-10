package com.liquido.base.pojo.vo;


import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryMerchantVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String code;

    private String uuid;

}
