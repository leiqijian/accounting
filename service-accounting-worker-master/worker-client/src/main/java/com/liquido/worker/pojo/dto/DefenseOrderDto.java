package com.liquido.worker.pojo.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.Convert;

import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.enums.ProductCodeEnum;
import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.worker.enums.DefenseStatusEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("PMD.TooManyFields")
public class DefenseOrderDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * original_id
     **/
    private String originalId;

    /**
     * from global transaction system uniqueId, reference task_fee_calculation.unique_id
     **/
    private String uniqueId;

    /**
     * fk
     **/
    private Long merchantId;

    /**
     * account id
     **/
    private Long accountId;

    /**
     * TransactionTypeCodeEnum: PAY_IN/PAY_OUT/MARKET_PLACE_ORDERS
     **/
    @Convert(converter = TransactionTypeCodeEnum.Convert.class)
    private TransactionTypeCodeEnum transactionTypeCode;

    /**
     * ProductCodeEnum: SPEI/TED/PIX/CREDIT_CARD/ELO_CREDIT_CARD/BOLETO/OXXO/GIFTCARD/TOPUP/UTILITY
     **/
    @Convert(converter = ProductCodeEnum.Convert.class)
    private ProductCodeEnum productCode;

    /**
     * Original transaction amount, unit: cent
     **/
    private BigDecimal paymentAmount;

    /**
     * transaction amount, unit: cent
     **/
    private BigDecimal disputeAmount;

    /**
     * transaction currency
     **/
    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum currency;

    /**
     * card number
     */
    private String cardNumber;

    /**
     * Original order transaction time
     */
    private LocalDateTime paymentTime;

    /**
     * UTC+0
     **/
    private LocalDateTime disputeTime;

    /**
     * status/(chargeback、under defense、defense won、defense lost)
     **/
    @Convert(converter = DefenseStatusEnum.Convert.class)
    private DefenseStatusEnum defenseStatus;

    /**
     * reason
     **/
    private String disputeReason;

    /**
     * refund results
     **/
    private String refundResults;

    /**
     * UTC+0
     **/
    private LocalDateTime defenseTime;

    /**
     * defend count down
     */
    private Integer daysLeftToDefend;

    /**
     * UTC+0 defense deadline
     **/
    private LocalDateTime defenseDeadline;

    /**
     * defense description
     **/
    private String defenseDescription;

    /**
     * defense appendix
     */
    private List<Long> defenseAppendixIds;

}
