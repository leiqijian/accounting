package com.liquido.worker.pojo.dto;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FxQuoteDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer code;

    private String msg;

    private List<ExchangeRateDto> timeRangeData;
}
