package com.liquido.aqueducts.vo.response;

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
public class ProcessingInfo {
    private Long creationDate;
    private String transferStatusCode;
    private String transferErrorMsg;
    private String paymentFlow;
    private Boolean use3ds;
    private Integer installments;
}
