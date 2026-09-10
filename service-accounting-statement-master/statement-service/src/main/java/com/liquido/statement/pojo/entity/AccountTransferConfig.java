package com.liquido.statement.pojo.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.base.enums.OperateModeEnum;
import com.liquido.core.common.snowflake.IdGeneratorStrategy;
import com.liquido.statement.enums.TransferConfigStateEnum;

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
@Table(name = "account_transfer_config")
@Where(clause = "del_flag = false")
@SuppressWarnings("PMD.TooManyFields")
public class AccountTransferConfig implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = IdGeneratorStrategy.SNOWFLAKE_GENERATOR)
    private Long id;

    @Column(name = "merchant_id")
    private Long merchantId;

    @Column(name = "payin_account_id")
    private Long payinAccountId;

    @Column(name = "payout_account_id")
    private Long payoutAccountId;

    @Column(name = "timer_cron")
    private String timerCron;

    @Column(name = "country_code")
    @Convert(converter = CountryCodeEnum.Convert.class)
    private CountryCodeEnum countryCode;

    @Column(name = "merchant_code")
    private String merchantCode;

    @Column(name = "operate_mode")
    @Convert(converter = OperateModeEnum.Convert.class)
    private OperateModeEnum operateMode;

    @Column(name = "state")
    @Convert(converter = TransferConfigStateEnum.Convert.class)
    private TransferConfigStateEnum state;

    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "updated_by")
    private Long updatedBy;

    /**
     * JPA Use javax.persistence.@Version achieve optimistic locking
     */
    //@Version
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
