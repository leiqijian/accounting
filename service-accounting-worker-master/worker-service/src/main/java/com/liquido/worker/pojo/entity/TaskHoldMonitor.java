package com.liquido.worker.pojo.entity;

import java.io.Serializable;
import java.math.BigDecimal;
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

@SuppressWarnings("PMD.TooManyFields")
@Getter
@Setter
@Entity
@Builder
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "task_holding_monitor")
public class TaskHoldMonitor implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = IdGeneratorStrategy.SNOWFLAKE_GENERATOR)
    private Long id;

    /**
     * 'belong month: yyyyMM
     */
    @Column(name = "monthly")
    private Integer monthly;

    @Column(name = "account_id")
    private Long accountId;

    /**
     * documentId certificate No, e.g: CPF, CNPJ
     */
    @Column(name = "document_id")
    private String documentId;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    /**
     * version optimistic locking
     */
    @Column(name = "version")
    private Integer version;

    @Column(name = "remark")
    private String remark;

    /**
     * createdTime timezone:UTC0
     */
    @Column(name = "created_time")
    private LocalDateTime createdTime;

    /**
     * updatedTime timezone:UTC0
     */
    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

}
