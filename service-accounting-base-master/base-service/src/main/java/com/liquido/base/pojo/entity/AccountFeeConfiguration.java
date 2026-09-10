package com.liquido.base.pojo.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.liquido.base.convert.CalculationRuleConverter;
import com.liquido.base.convert.ListToCurrencyConvert;
import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.enums.DirectionTypeEnum;
import com.liquido.base.enums.FeeCodeEnum;
import com.liquido.base.enums.FeeGroupEnum;
import com.liquido.base.enums.FeeOnEnum;
import com.liquido.base.enums.FeeTypeCodeEnum;
import com.liquido.base.enums.FeeValueModelEnum;
import com.liquido.base.enums.MonthlyVolumeTypeEnum;
import com.liquido.base.enums.ProductCodeEnum;
import com.liquido.core.common.snowflake.IdGeneratorStrategy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Where;

@SuppressWarnings("PMD.TooManyFields")
@Getter
@Setter
@Entity
@Builder
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "account_fee_configuration")
@Where(clause = "del_flag = false")
public class AccountFeeConfiguration implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = IdGeneratorStrategy.SNOWFLAKE_GENERATOR)
    private Long id;

    /**
     * fk
     */
    @Column(name = "account_id")
    private Long accountId;

    /**
     * fk
     */
    @Column(name = "account_product_id")
    private Long accountProductId;


    /**
     * ProductCodeEnum: SPEI/TED/PIX/CREDIT_CARD/ELO_CREDIT_CARD/BOLETO/OXXO/GIFTCARD/TOPUP/UTILITY
     */
    @Column(name = "product_code")
    @Convert(converter = ProductCodeEnum.Convert.class)
    private ProductCodeEnum productCode;

    @Convert(converter = CalculationRuleConverter.class)
    @Column(name = "calculation_rule", columnDefinition = "json")
    private Map<String, String> calculationRule;

    @Column(name = "fee_name")
    private String feeName;

    @Column(name = "fee_code")
    @Convert(converter = FeeCodeEnum.Convert.class)
    private FeeCodeEnum feeCode;

    /**
     * FeeTypeCodeEnum: TRANSACTION_FEE/FX/TAX/REFUND_FEE/CHARGE_BACK_FEE/WITHDRAW_FEE
     */
    @Column(name = "fee_type_code")
    @Convert(converter = FeeTypeCodeEnum.Convert.class)
    private FeeTypeCodeEnum feeTypeCode;

    /**
     * Since for Report classification summary statistics
     */
    @Column(name = "fee_group")
    @Convert(converter = FeeGroupEnum.Convert.class)
    private FeeGroupEnum feeGroup;

    /**
     * MonthlyVolumeTypeEnum: 0-amount/1-counts
     */
    @Column(name = "monthly_volume_type")
    @Convert(converter = MonthlyVolumeTypeEnum.Convert.class)
    private MonthlyVolumeTypeEnum monthlyVolumeType;

    @Column(name = "min_monthly_volume")
    private BigDecimal minMonthlyVolume;

    @Column(name = "max_monthly_volume")
    private BigDecimal maxMonthlyVolume;

    /**
     * Min volume(Limit single transaction amount)
     */
    @Column(name = "min_volume")
    private BigDecimal minVolume;

    /**
     * Max volume(Limit single transaction amount)
     */
    @Column(name = "max_volume")
    private BigDecimal maxVolume;

    /**
     * FeeValueModelEnum: fixed -0/percent -1
     */
    @Column(name = "fee_value_model")
    @Convert(converter = FeeValueModelEnum.Convert.class)
    private FeeValueModelEnum feeValueModel;

    /**
     * fixed value or percentage ratio
     */
    @Column(name = "fee_value")
    private BigDecimal feeValue;

    @Column(name = "account_currency")
    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum accountCurrency;

    @Column(name = "source_currency")
    @Convert(converter = ListToCurrencyConvert.class)
    private List<CurrencyEnum> sourceCurrency;

    @Column(name = "min_fee_amount")
    private BigDecimal minFeeAmount;

    @Column(name = "max_fee_amount")
    private BigDecimal maxFeeAmount;

    /**
     * FeeOnEnum: AMOUNT/FEE
     */
    @Column(name = "fee_on")
    @Convert(converter = FeeOnEnum.Convert.class)
    private FeeOnEnum feeOn;

    /**
     * DirectionTypeEnum: SETTLED/REFUND/CHARGE_BACK
     */
    @Column(name = "direction_type")
    @Convert(converter = DirectionTypeEnum.Convert.class)
    private DirectionTypeEnum directionType;

    /**
     * 0-non-instant，1-instant
     */
    @Column(name = "instant_flag")
    private Boolean instantFlag;

    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "updated_by")
    private Long updatedBy;

    @Column(name = "version")
    private Integer version;

    /**
     * 0-normal，1-delete
     */
    @Column(name = "del_flag")
    private Boolean delFlag;

    @Column(name = "remark")
    private String remark;

}
