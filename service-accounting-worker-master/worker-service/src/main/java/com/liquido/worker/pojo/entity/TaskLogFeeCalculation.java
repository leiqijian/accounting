package com.liquido.worker.pojo.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.liquido.core.common.snowflake.IdGeneratorStrategy;
import com.liquido.worker.enums.TaskLogResultEnum;

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
@Table(name = "task_log_fee_calculation")
public class TaskLogFeeCalculation implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = IdGeneratorStrategy.SNOWFLAKE_GENERATOR)
    private Long id;

    /**
     * reference TaskFeeCalculation.id
     */
    @Column(name = "request_id")
    private String requestId;

    /**
     * reference TaskFeeCalculation.id
     */
    @Column(name = "task_id")
    private Long taskId;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_Time")
    private LocalDateTime endTime;

    /**
     * unit:ms
     */
    @Column(name = "calculate_consuming")
    private Long calculateConsuming;

    /**
     * unit:ms
     */
    @Column(name = "settlement_consuming")
    private Long settlementConsuming;

    /**
     * unit:ms
     */
    @Column(name = "total_consuming")
    private Long totalConsuming;

    @Column(name = "batch_count")
    private Integer batchCount;

    /**
     * FAILED; SUCCESS;
     */
    @Column(name = "task_result")
    @Convert(converter = TaskLogResultEnum.Convert.class)
    private TaskLogResultEnum taskResult;

    @Column(name = "request_data")
    private String requestData;

    @Column(name = "response_data")
    private String responseData;

    @Column(name = "created_time")
    private LocalDateTime createdTime;

}
