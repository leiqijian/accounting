package com.liquido.statement.pojo.dto;

import java.io.Serializable;
import javax.persistence.Convert;

import com.liquido.base.enums.SettleStatusEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountSettlementDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long transactionId;

    @Convert(converter = SettleStatusEnum.Convert.class)
    private SettleStatusEnum settleStatus;

}
