package com.liquido.aqueducts.vo.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PayinCardDetail extends PayinDefaultDetail {
    private PayerInfo payer;
    private CardInfo card;
    private ProcessingInfo information;
}
