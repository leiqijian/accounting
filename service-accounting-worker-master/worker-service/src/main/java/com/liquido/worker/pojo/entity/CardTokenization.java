package com.liquido.worker.pojo.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.base.enums.VendorCodeEnum;
import com.liquido.core.common.snowflake.IdGeneratorStrategy;
import com.liquido.worker.enums.VersionEnum;

import com.fasterxml.jackson.databind.JsonNode;
import com.vladmihalcea.hibernate.type.json.JsonStringType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
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
@Table(name = "card_tokenization")
@Where(clause = "del_flag = false")
@TypeDef(name = "json", typeClass = JsonStringType.class)
public class CardTokenization implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = IdGeneratorStrategy.SNOWFLAKE_GENERATOR)
    private Long id;

    /**
     * Token unique id
     */
    @Column(name = "token_id", unique = true, nullable = false)
    private String tokenId;

    @Column(name = "token_md5", unique = true, nullable = false)
    private String tokenMd5;

    @Column(name = "merchant_code", nullable = false)
    private String merchantCode;

    @Column(name = "tokenization_created_time")
    private LocalDateTime tokenizationCreatedTime;

    @Column(name = "tokenization_created_timestamp")
    private Long tokenizationCreatedTimestamp;

    @Column(name = "country_code", nullable = false)
    @Convert(converter = CountryCodeEnum.Convert.class)
    private CountryCodeEnum countryCode;

    @Column(name = "bin", nullable = false)
    private String bin;

    @Column(name = "brand", nullable = false)
    private String brand;

    @Column(name = "last_4_digit", nullable = false)
    private String last4Digit;

    @Column(name = "card_holder_name", nullable = false)
    private String cardHolderName;

    @Column(name = "expiration_year", nullable = false)
    private BigDecimal expirationYear;

    @Column(name = "expiration_month", nullable = false)
    private BigDecimal expirationMonth;

    @Column(name = "vendor", nullable = false)
    @Convert(converter = VendorCodeEnum.Convert.class)
    private VendorCodeEnum vendor;

    @Column(name = "token_info")
    @Type(type = "json")
    private JsonNode tokenInfo;

    /**
     * timestamp UTC+0
     */
    @Column(name = "event_timestamp", nullable = false)
    private Long eventTimestamp;

    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

    /**
     * optimistic locking : 0-normal,1-locked
     */
    private VersionEnum version;

    /**
     * 0-normal,1-delete
     */
    @Column(name = "del_flag", nullable = false)
    private Boolean delFlag;

}
