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
public class QueryDailyExchangeRateListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * exchange beginTime UTC+0 time
     */
    @NotNull
    private LocalDateTime beginTime;

    /**
     * exchange endTime UTC+0 time
     */
    @NotNull
    private LocalDateTime endTime;

    /**
     * account id
     */
    private List<Long> accountIds;

}
