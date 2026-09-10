package com.liquido.base.pojo.dto;

import java.io.Serializable;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkingDayDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private LocalDate workDate;

    /**
     * Day of week 1:MONDAY, 2:TUESDAY, 3:WEDNESDAY, ... 7:SUNDAY
     */
    private Integer weekday;

    /**
     * is workday, 0:FALSE, 1:TRUE
     */
    private Boolean workday;

}
