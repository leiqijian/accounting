package com.liquido.statement.pojo.vo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchAddExchangeRateVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * obtain exchange datetime(UTC+0),
     * format: yyyy-MM-dd HH:mm:ss
     */
    @NotNull
    private LocalDateTime obtainTime;

    @NotNull
    private List<RealTimeExchangeRateVo> realTimeExchangeRateList;

    @NotNull
    private List<DailyExchangeRateVo> accountExchangeRateList;

}
