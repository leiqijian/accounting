package com.liquido.statement.pojo.vo;

import java.io.Serializable;
import java.util.List;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateDailyBillVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * account after process daily cut billId
     */
    @NotNull
    private List<Long> billIdList;

}
