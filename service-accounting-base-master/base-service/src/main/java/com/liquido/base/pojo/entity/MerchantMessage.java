package com.liquido.base.pojo.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.base.enums.MessageSourceTypeEnum;
import com.liquido.base.enums.MessageTypeEnum;
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
@Table(name = "merchant_message")
@Where(clause = "del_flag = false")
public class MerchantMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = IdGeneratorStrategy.SNOWFLAKE_GENERATOR)
    private Long id;

    @Column(name = "merchant_id")
    private Long merchantId;

    @Column(name = "country_code")
    @Convert(converter = CountryCodeEnum.Convert.class)
    private CountryCodeEnum countryCode;

    @Column(name = "message_source_type")
    @Convert(converter = MessageSourceTypeEnum.Convert.class)
    private MessageSourceTypeEnum messageSourceType;

    @Column(name = "message_type")
    @Convert(converter = MessageTypeEnum.Convert.class)
    private MessageTypeEnum messageType;

    @Column(name = "message")
    private String message;

    @Column(name = "event_time")
    private LocalDateTime eventTime;

    @Column(name = "del_flag")
    private Boolean delFlag;

}
