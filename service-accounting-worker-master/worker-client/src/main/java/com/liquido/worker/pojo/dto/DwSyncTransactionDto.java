package com.liquido.worker.pojo.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("PMD.TooManyFields")
public class DwSyncTransactionDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * unique id
     */
    private String uniqueId;

    /**
     * merchant code: examples cheng_fan
     */
    private String merchantCode;

    /**
     * merchant reference
     */
    private String merchantReference;

    /**
     * transaction type: PAY_IN/PAY_OUT/MARKET_PLACE_ORDERS
     */
    private String transactionType;

    /**
     * product code: SPEI/TED/PIX/CREDIT_CARD/BOLETO/OXXO
     */
    private String productCode;

    private BigDecimal amount;

    private String currency;

    private String country;

    /**
     * transaction status: IN_PROGRESS/SETTLED/FAILED/CHARGED_BACK/REFUNDING/REFUNDED/EXPIRED
     */
    private String transactionStatus;

    /**
     * SETTLED/REFUND/CHARGE_BACK
     */
    private String directionCode;

    /**
     * just for Brazil user certificate No, e.g: CPF, CNPJ
     */
    private String documentId;

    /**
     * trade system data: transfer_status/status
     */
    private String tradeTransferStatus;

    /**
     * trade system data: transaction_type
     */
    private String tradeTransactionType;

    /**
     * vendor
     */
    private String vendor;

    /**
     * subMerchantId
     */
    private String subMerchantId;

    /**
     * final timestamp
     */
    private Long finalStatusTime;

    /**
     * event happen timestamp
     */
    private Long eventTime;

    /**
     * submit timestamp
     */
    private Long submitTime;

    /**
     * order create timestamp
     */
    private Long createTime;

    /**
     * is test data ?
     */
    private Boolean flags;

    private String transactionDataSource;

    /**
     * value from virgo.payment.payer_comment or gemini.payer_comment
     * others.payerComment = this.description
     */
    private String description;

    /**
     * others info
     */
    private ObjectNode others;

}
