package com.liquido.worker.pojo.vo;

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
public class ReRunTransactionDailyBillVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    private Long merchantId;

    @NotNull
    private List<Long> accountIdList;

    @NotNull
    private LocalDateTime beginUtcDate;

    @NotNull
    private LocalDateTime endUtcDate;

}
