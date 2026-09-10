package com.liquido.statement.pojo.vo;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchWithdrawalApplyVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull
    private Long merchantId;

    @NotNull
    private Long accountId;

    @NotNull
    private LocalDate creditedDate;

    private Set<String> subMerchantId;

}
