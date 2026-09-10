package com.liquido.base.pojo.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.liquido.base.enums.CardTypeEnum;
import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.base.enums.CreditCardGroupCodeEnum;
import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.enums.DirectionTypeEnum;
import com.liquido.base.enums.FeeGroupEnum;
import com.liquido.base.enums.FeeOnEnum;
import com.liquido.base.enums.FeeTypeCodeEnum;
import com.liquido.base.enums.FeeValueModelEnum;
import com.liquido.base.enums.VendorCodeEnum;
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

@Getter
@Setter
@Entity
@Builder
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cost_configuration_card")
@Where(clause = "del_flag = false")
public class CardCostConfiguration implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = IdGeneratorStrategy.SNOWFLAKE_GENERATOR)
    private Long id;

    @Column(name = "active_version")
    private Integer activeVersion;

    @Column(name = "account_id")
    private Long accountId;

    /**
     * CountryCodeEnum: BR/MX/US
     */
    @Column(name = "country_code")
    @Convert(converter = CountryCodeEnum.Convert.class)
    private CountryCodeEnum countryCode;

    @Column(name = "vendor_code")
    @Convert(converter = VendorCodeEnum.Convert.class)
    private VendorCodeEnum vendorCode;

    @Column(name = "direction_type")
    @Convert(converter = DirectionTypeEnum.Convert.class)
    private DirectionTypeEnum directionType;

    // DEBIT_CARD, CREDIT_CARD
    @Column(name = "card_type")
    @Convert(converter = CardTypeEnum.Convert.class)
    private CardTypeEnum cardType;

    @Column(name = "card_group")
    @Convert(converter = CreditCardGroupCodeEnum.Convert.class)
    private CreditCardGroupCodeEnum cardGroup;

    @Column(name = "installment_begin")
    private Integer installmentBegin;

    @Column(name = "installment_end")
    private Integer installmentEnd;

    @Column(name = "fee_name")
    private String feeName;

    @Column(name = "fee_type")
    @Convert(converter = FeeTypeCodeEnum.Convert.class)
    private FeeTypeCodeEnum feeType;

    @Column(name = "fee_group")
    @Convert(converter = FeeGroupEnum.Convert.class)
    private FeeGroupEnum feeGroup;

    @Column(name = "fee_on")
    @Convert(converter = FeeOnEnum.Convert.class)
    private FeeOnEnum feeOn;

    @Column(name = "fee_model")
    @Convert(converter = FeeValueModelEnum.Convert.class)
    private FeeValueModelEnum feeModel;

    @Column(name = "volume")
    private BigDecimal volume;

    @Column(name = "min_volume")
    private BigDecimal minVolume;

    @Column(name = "max_volume")
    private BigDecimal maxVolume;

    @Column(name = "currency")
    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum currency;

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

    @Column(name = "del_flag")
    private Boolean delFlag;

    @Column(name = "remark")
    private String remark;

}
