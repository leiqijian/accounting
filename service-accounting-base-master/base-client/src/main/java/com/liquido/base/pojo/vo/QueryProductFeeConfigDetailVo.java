package com.liquido.base.pojo.vo;

import java.io.Serializable;
import java.util.Map;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryProductFeeConfigDetailVo implements Serializable {

    private static final long serialVersionUID = -9077335737284491157L;

    @NotNull
    private Long accountProductId;

    private Map<String, String> calculationRule;

}
