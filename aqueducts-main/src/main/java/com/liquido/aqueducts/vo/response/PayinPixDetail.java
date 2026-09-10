package com.liquido.aqueducts.vo.response;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PayinPixDetail extends PayinDefaultDetail {
    private PayerInfo payer;
    private PixCredentials pixCredentials;
    private Map<String, Object> refundAccountInfo;
}
