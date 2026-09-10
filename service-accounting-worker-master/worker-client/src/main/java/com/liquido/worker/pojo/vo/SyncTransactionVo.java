package com.liquido.worker.pojo.vo;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Convert;
import javax.validation.constraints.NotNull;

import com.liquido.base.enums.TransactionTypeCodeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyncTransactionVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Convert(converter = TransactionTypeCodeEnum.Convert.class)
    private TransactionTypeCodeEnum typeCodeEnum;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private String merchantCode;

}
