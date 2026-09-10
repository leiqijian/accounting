package com.liquido.worker.pojo.dto;

import java.io.Serializable;
import javax.persistence.Convert;

import com.liquido.base.enums.TransactionStatusEnum;
import com.liquido.base.enums.VendorCodeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryStatusByDetailDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long workOrderId;

    private String subAccountId;

    private String trackingId;

    private String uniqueId;

    @Convert(converter = TransactionStatusEnum.Convert.class)
    private TransactionStatusEnum transactionStatus;

    @Convert(converter = VendorCodeEnum.Convert.class)
    private VendorCodeEnum vendor;

}
