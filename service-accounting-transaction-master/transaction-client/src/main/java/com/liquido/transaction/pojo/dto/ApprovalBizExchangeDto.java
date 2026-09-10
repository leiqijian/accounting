package com.liquido.transaction.pojo.dto;


import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.Convert;

import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.enums.OwnerEnum;
import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.core.common.logger.SensitiveField;
import com.liquido.core.common.logger.SensitiveType;
import com.liquido.transaction.enums.ApprovalBizExchangeStatusEnum;
import com.liquido.transaction.enums.ExchangeAccountTypeEnum;
import com.liquido.transaction.pojo.bo.ApprovalRemarkBo;
import com.liquido.transaction.pojo.bo.BeneficiaryAccountBo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("PMD.TooManyFields")
public class ApprovalBizExchangeDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long approvalId;

    /**
     * merchant info: id
     */
    private Long merchantId;

    /**
     * merchant info: code
     */
    private String merchantCode;

    /**
     * merchant info: name
     */
    private String merchantName;

    @Convert(converter = OwnerEnum.Convert.class)
    private OwnerEnum owner;

    /**
     * country: CountryCodeEnum
     */
    @Convert(converter = CountryCodeEnum.Convert.class)
    private CountryCodeEnum countryCode;

    /**
     * transaction type: TransactionTypeCodeEnum
     */
    @Convert(converter = TransactionTypeCodeEnum.Convert.class)
    private TransactionTypeCodeEnum transactionTypeCode;

    @Convert(converter = ExchangeAccountTypeEnum.Convert.class)
    private ExchangeAccountTypeEnum exchangeAccountType;

    /**
     * account info : id
     */
    private Long accountId;

    /**
     * The source currency, such as BRL, MXN
     */
    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum sourceCurrency;

    /**
     * The target currency, that merchants want to change, such as USD, EUR
     */
    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum targetCurrency;

    /**
     * exchange rate
     */
    private BigDecimal exchangeRate;

    /**
     * merchant ratio lose
     */
    private BigDecimal ratioLose;

    /**
     * the final rate used by the merchant
     */
    private BigDecimal merchantRate;

    /**
     * merchant exchange amount (unit: cent)
     */
    private BigDecimal exchangeAmount;

    /**
     * merchant exchange amount converted into TargetCurrency (unit: cent)
     */
    private BigDecimal exchangeAmountTargetCurrency;

    /**
     * merchant exchange amount converted into TargetCurrency (unit: cent)
     */
    private BigDecimal actualExchangeAmountTargetCurrency;

    /**
     * exchange currency
     */
    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum exchangeCurrency;

    private BigDecimal requestedAmount;

    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum requestedCurrency;

    /**
     * smallTransferFee (unit: cent)
     */
    private BigDecimal smallTransferFee;

    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum smallTransferFeeCurrency;

    /**
     * merchant beneficiary bank
     */
    private BeneficiaryAccountBo beneficiaryAccount;

    @Convert(converter = ApprovalBizExchangeStatusEnum.Convert.class)
    private ApprovalBizExchangeStatusEnum status;

    private Integer actionDoneFlag;

    /**
     * merchant user info: id
     */
    private Long merchantUserId;

    /**
     * merchant user info: name
     */
    @SensitiveField(SensitiveType.REAL_NAME)
    private String merchantUserName;

    /**
     * merchant user info: email
     */
    @SensitiveField(SensitiveType.EMAIL)
    private String merchantUserEmail;

    private String subMerchantId;

    private String subMerchantName;

    private String merchantRemark;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;

    private Boolean revokeFlag;

    private List<ApprovalRemarkBo> remark;

    private List<ApprovalNodeSimpleDto> nodes;

}
