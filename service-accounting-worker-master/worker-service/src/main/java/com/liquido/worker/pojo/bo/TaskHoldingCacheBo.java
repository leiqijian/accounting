package com.liquido.worker.pojo.bo;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskHoldingCacheBo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String documentId;

    private BigDecimal totalAmount;

    private Boolean overLimit;

}
