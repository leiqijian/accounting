package com.liquido.worker.aws.sqs.msg;

import java.util.List;
import javax.persistence.Convert;

import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.statement.pojo.vo.TransactionInProgressVo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceTransactionInProgressSyncMsg {

    private Long syncId;

    /**
     * merchantCode_country_transactionTypeCode
     */
    private String accountIdentifier;

    private String merchantCode;

    @Convert(converter = CountryCodeEnum.Convert.class)
    private CountryCodeEnum countryCode;

    @Convert(converter = TransactionTypeCodeEnum.Convert.class)
    private TransactionTypeCodeEnum transactionTypeCode;

    private List<TransactionInProgressVo> inProgressData;

}
