package com.liquido.base.pojo.dto;

import java.io.Serializable;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeeCodeWeightDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Map<String, Integer> weights;

}
