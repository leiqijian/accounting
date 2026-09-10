package com.liquido.worker.pojo.bo;


import java.io.Serializable;
import java.util.List;

import com.liquido.statement.pojo.vo.RealTimeExchangeRateVo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RealTimeExchangeRateBo implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<RealTimeExchangeRateVo> supplementaryRealTimeExchangeRateList;

    private List<RealTimeExchangeRateVo> allRealTimeExchangeRateList;
}
