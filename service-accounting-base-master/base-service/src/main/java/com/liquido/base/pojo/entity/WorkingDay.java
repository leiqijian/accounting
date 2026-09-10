package com.liquido.base.pojo.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.liquido.base.enums.CountryCodeEnum;
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
@Table(name = "working_day")
@Where(clause = "del_flag = false")
public class WorkingDay implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = IdGeneratorStrategy.SNOWFLAKE_GENERATOR)
    private Long id;

    @Column(name = "country")
    @Convert(converter = CountryCodeEnum.Convert.class)
    private CountryCodeEnum country;

    @Column(name = "work_date")
    private LocalDate workDate;

    /**
     * Day of week 1:MONDAY, 2:TUESDAY, 3:WEDNESDAY, ... 7:SUNDAY
     */
    @Column(name = "weekday")
    private Integer weekday;

    /**
     * is workday, 0:FALSE, 1:TRUE
     */
    @Column(name = "workday")
    private Boolean workday;

    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

    /**
     * optimistic locking
     */
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
