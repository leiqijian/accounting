package com.liquido.base.pojo.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountProductVersionDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * fk
     */
    private Long accountId;

    /**
     * fk
     */
    private Long accountProductId;

    private Integer activeMonth;

    private LocalDate startDate;

    private LocalDate endDate;

    private Integer accountFeeVersion;

    private Integer monthlyFeeVersion;

    private Boolean monthlyFlag;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;

    private Boolean delFlag;

    private String remark;

}
