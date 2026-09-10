package com.liquido.base.pojo.vo;

import java.io.Serializable;
import java.time.LocalDate;
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
public class AccountProductVersionVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * fk
     */
    @NotNull
    private Long accountId;

    /**
     * fk
     */
    @NotNull
    private Long accountProductId;

    @NotNull
    private Integer activeMonth;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    @NotNull
    private Integer accountFeeVersion;

    @NotNull
    private Integer monthlyFeeVersion;

    @NotNull
    private Boolean monthlyFlag;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;

    private Boolean delFlag;

    private String remark;

}
