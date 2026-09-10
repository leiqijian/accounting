package com.liquido.statement.pojo.vo;

import java.io.Serializable;
import java.util.List;
import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchQueryAccountBalanceVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotEmpty
    private List<Long> merchantIds;

}
