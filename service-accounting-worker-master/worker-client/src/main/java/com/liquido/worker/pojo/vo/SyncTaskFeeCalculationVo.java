package com.liquido.worker.pojo.vo;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Convert;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.liquido.base.enums.TransactionTypeCodeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SyncTaskFeeCalculationVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Convert(converter = TransactionTypeCodeEnum.Convert.class)
    private TransactionTypeCodeEnum typeCodeEnum;

    @NotNull
    @Min(1)
    private Long from;

    @NotNull
    @Min(1)
    private Long to;

    /**
     * [ merchantCode_country_transactionType ]
     */
    private List<String> filters;

    private Long extension;

    private Boolean useFinalStatusTime;

}
