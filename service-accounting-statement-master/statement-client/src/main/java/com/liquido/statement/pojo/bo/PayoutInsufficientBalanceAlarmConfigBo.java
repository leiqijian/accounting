package com.liquido.statement.pojo.bo;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayoutInsufficientBalanceAlarmConfigBo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Boolean flag;
}
