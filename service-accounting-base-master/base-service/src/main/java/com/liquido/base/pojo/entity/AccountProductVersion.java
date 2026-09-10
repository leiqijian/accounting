package com.liquido.base.pojo.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

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
@Table(name = "account_product_version")
@Where(clause = "del_flag = false")
public class AccountProductVersion implements Serializable {
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


    @Column(name = "active_month")
    private Integer activeMonth;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "account_fee_version")
    private Integer accountFeeVersion;

    @Column(name = "monthly_fee_version")
    private Integer monthlyFeeVersion;

    @Column(name = "monthly_flag")
    private Boolean monthlyFlag;

    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

    @Column(name = "del_flag")
    private Boolean delFlag;

    @Column(name = "remark")
    private String remark;

}
