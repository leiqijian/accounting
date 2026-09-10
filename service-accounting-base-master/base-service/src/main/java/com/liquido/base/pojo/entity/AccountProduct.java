package com.liquido.base.pojo.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.liquido.base.enums.MonthlyVolumeTypeEnum;
import com.liquido.base.enums.ProductCodeEnum;
import com.liquido.base.enums.TradingModelEnum;
import com.liquido.base.enums.TransactionTypeCodeEnum;
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
@Table(name = "account_product")
@Where(clause = "del_flag = false")
public class AccountProduct implements Serializable {
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
     * TransactionTypeCodeEnum: PAY_IN/PAY_OUT/MARKET_PLACE_ORDERS
     */
    @Column(name = "transaction_type_code")
    @Convert(converter = TransactionTypeCodeEnum.Convert.class)
    private TransactionTypeCodeEnum transactionTypeCode;

    /**
     * ProductCodeEnum: SPEI/TED/PIX/CREDIT_CARD/ELO_CREDIT_CARD/BOLETO/OXXO/GIFTCARD/TOPUP/UTILITY
     */
    @Column(name = "product_code")
    @Convert(converter = ProductCodeEnum.Convert.class)
    private ProductCodeEnum productCode;

    @Column(name = "trading_model")
    @Convert(converter = TradingModelEnum.Convert.class)
    private TradingModelEnum tradingModel;

    /**
     * MonthlyVolumeTypeEnum: 0-amount/1-counts
     */
    @Column(name = "monthly_volume_type")
    @Convert(converter = MonthlyVolumeTypeEnum.Convert.class)
    private MonthlyVolumeTypeEnum monthlyVolumeType;

    /**
     * Account group statistics
     */
    @Column(name = "monthly_volume_group")
    private String monthlyVolumeGroup;

    /**
     * redundant account time zone
     */
    @Column(name = "timezone")
    private String timezone;

    @Column(name = "open_time")
    private LocalDateTime openTime;

    @Column(name = "remark")
    private String remark;

    /**
     * 0-normal，1-delete
     */
    @Column(name = "del_flag")
    private Boolean delFlag;

}
