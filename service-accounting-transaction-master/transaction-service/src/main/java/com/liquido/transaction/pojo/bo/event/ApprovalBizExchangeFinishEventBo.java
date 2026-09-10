package com.liquido.transaction.pojo.bo.event;


import java.io.Serializable;

import com.liquido.transaction.enums.ApprovalStatusEnum;
import com.liquido.transaction.pojo.entity.ApprovalBizExchange;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalBizExchangeFinishEventBo implements Serializable {

    private static final long serialVersionUID = 1L;

    private ApprovalBizExchange exchange;

    private ApprovalStatusEnum approvalStatus;

    private Boolean executeResult;

    private String message;
}
