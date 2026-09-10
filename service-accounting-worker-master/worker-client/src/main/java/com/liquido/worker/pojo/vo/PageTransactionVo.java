package com.liquido.worker.pojo.vo;

import java.time.LocalDateTime;
import javax.persistence.Convert;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.base.enums.ProductCodeEnum;
import com.liquido.base.enums.TransactionStatusEnum;
import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.core.mvc.vo.PageCondition;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("PMD.TooManyFields")
public class PageTransactionVo extends PageCondition {
    private static final long serialVersionUID = 1L;

    @NotNull
    @Convert(converter = CountryCodeEnum.Convert.class)
    private CountryCodeEnum country;

    @NotBlank
    private String merchantCode;

    @NotBlank
    private String merchantName;

    @NotNull
    @Convert(converter = TransactionTypeCodeEnum.Convert.class)
    private TransactionTypeCodeEnum transactionType;

    /**
     * start date of the UTC(Submit Time)
     */
    private LocalDateTime startDate;

    /**
     * end date of the UTC(Submit Time)
     */
    private LocalDateTime endDate;

    /**
     * start date of the UTC(Transaction Time)
     */
    private LocalDateTime startTransactionDate;

    /**
     * end date of the UTC(Transaction Time)
     */
    private LocalDateTime endTransactionDate;

    /**
     * Search param
     */
    private String merchantReference;

    /**
     * Search param
     */
    @Convert(converter = TransactionStatusEnum.Convert.class)
    private TransactionStatusEnum status;

    /**
     * Search param
     */
    @Convert(converter = ProductCodeEnum.Convert.class)
    private ProductCodeEnum product;

    /**
     * trade system data: transfer_status/status
     */
    private String tradeTransferStatus;

    /**
     * trade system data: transaction_type
     */
    private String tradeTransactionType;

    /**
     * Payer/Payee
     */
    private String accountName;

    private Boolean holdStatus;

    private String subMerchantId;

    private String description;

}
