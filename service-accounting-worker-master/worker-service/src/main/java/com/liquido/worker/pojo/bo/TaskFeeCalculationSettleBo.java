package com.liquido.worker.pojo.bo;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Convert;

import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.base.enums.TransactionTypeCodeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"merchantCode", "countryCode",
        "transactionTypeCode", "isSettleFlg", "date"})
public class TaskFeeCalculationSettleBo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * unique requestId
     */
    private String requestId;

    private Long syncId;

    private Long retryId;

    private String merchantCode;

    @Convert(converter = CountryCodeEnum.Convert.class)
    private CountryCodeEnum countryCode;

    @Convert(converter = TransactionTypeCodeEnum.Convert.class)
    private TransactionTypeCodeEnum transactionTypeCode;

    private Boolean isSettleFlg;

    private String date;

    private List<Long> taskIdList;
}
