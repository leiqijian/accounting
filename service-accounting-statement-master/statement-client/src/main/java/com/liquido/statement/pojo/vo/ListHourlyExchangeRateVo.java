package com.liquido.statement.pojo.vo;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListHourlyExchangeRateVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull
    private LocalDateTime exchangeTime;
}
