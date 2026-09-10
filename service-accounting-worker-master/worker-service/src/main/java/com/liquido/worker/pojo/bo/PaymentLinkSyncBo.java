package com.liquido.worker.pojo.bo;

import java.io.Serializable;
import java.util.List;

import com.liquido.worker.pojo.dto.DwSyncPaymentLinkDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"merchantCode", "country"})
public class PaymentLinkSyncBo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String merchantCode;

    private String country;

    private List<DwSyncPaymentLinkDto> bos;

}
