package com.liquido.base.pojo.vo;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListAccountMonthFxLoseVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private LocalDateTime activeTime;
}
