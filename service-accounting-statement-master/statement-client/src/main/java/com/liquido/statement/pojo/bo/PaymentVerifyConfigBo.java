package com.liquido.statement.pojo.bo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import com.liquido.core.common.logger.SensitiveField;
import com.liquido.core.common.logger.SensitiveType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentVerifyConfigBo implements Serializable {

    private static final long serialVersionUID = 1L;

    @SensitiveField(SensitiveType.EMAIL)
    private List<String> verificationMails;

    /**
     * Unit: Cent
     */
    private BigDecimal limitAmount;

}
