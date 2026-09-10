package com.liquido.worker.pojo.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.base.enums.ProductCodeEnum;
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

@Getter
@Setter
@Entity
@Builder
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transaction_ratio")
public class TransactionRatio {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = IdGeneratorStrategy.SNOWFLAKE_GENERATOR)
    private Long id;

    @Column(name = "merchant_code", nullable = false)
    private String merchantCode;

    @Convert(converter = CountryCodeEnum.Convert.class)
    @Column(name = "country_code", nullable = false)
    private CountryCodeEnum country;

    @Column(name = "transaction_type", nullable = false)
    @Convert(converter = TransactionTypeCodeEnum.Convert.class)
    private TransactionTypeCodeEnum transactionType;

    @Column(name = "product_code", nullable = false)
    @Convert(converter = ProductCodeEnum.Convert.class)
    private ProductCodeEnum productCode;

    private Integer totalCount;

    private Integer successCount;

    private BigDecimal successRate;

    private LocalDateTime date;

    @Column(name = "created_time", nullable = false)
    private LocalDateTime createTime;

    @Column(name = "del_flag", nullable = false)
    private Boolean delFlag;
}
