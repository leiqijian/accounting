package com.liquido.statement.pojo.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Convert;

import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.statement.enums.TransferConfigStateEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountTransferConfigDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private Long merchantId;

    private CountryCodeEnum countryCode;

    private String merchantCode;

    private Long payinAccountId;

    private Long payoutAccountId;

    private String timerCron;

    @Convert(converter = TransferConfigStateEnum.Convert.class)
    private TransferConfigStateEnum state;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;

    private String remark;

}
