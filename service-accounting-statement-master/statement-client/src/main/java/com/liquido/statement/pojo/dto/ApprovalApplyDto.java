package com.liquido.statement.pojo.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalApplyDto implements Serializable {
    private static final long serialVersionUID = 1L;

    public Long transactionId;

}
