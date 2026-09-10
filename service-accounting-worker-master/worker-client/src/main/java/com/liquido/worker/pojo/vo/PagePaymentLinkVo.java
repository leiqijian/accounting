package com.liquido.worker.pojo.vo;

import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.Convert;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.base.enums.DataSyncRefundStatusEnum;
import com.liquido.base.enums.DataSyncStatusEnum;
import com.liquido.core.mvc.vo.PageCondition;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagePaymentLinkVo extends PageCondition {

    private static final long serialVersionUID = 1L;

    @NotBlank
    private String merchantCode;

    @NotNull
    @Convert(converter = CountryCodeEnum.Convert.class)
    private CountryCodeEnum countryCode;

    /**
     * start date of the UTC(Submit Time)
     */
    private LocalDateTime startDate;

    /**
     * end date of the UTC(Submit Time)
     */
    private LocalDateTime endDate;

    /**
     * payment link unique id
     */
    private String linkId;

    private String subMerchantId;

    /**
     * merchant's order id
     */
    private List<String> merchantReferences;

    /**
     * Search param
     */
    @Convert(converter = DataSyncStatusEnum.Convert.class)
    private DataSyncStatusEnum paymentStatus;

    @Convert(converter = DataSyncRefundStatusEnum.Convert.class)
    private DataSyncRefundStatusEnum refundStatus;

    private String description;

}
