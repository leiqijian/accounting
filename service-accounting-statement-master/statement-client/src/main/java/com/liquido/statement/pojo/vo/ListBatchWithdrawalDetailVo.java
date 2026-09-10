package com.liquido.statement.pojo.vo;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListBatchWithdrawalDetailVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long batchId;

}
