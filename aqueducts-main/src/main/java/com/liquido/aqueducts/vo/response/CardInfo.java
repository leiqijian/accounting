package com.liquido.aqueducts.vo.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardInfo {
    private String paymentMethod;
    private String bin;
    private String last4;
    private String brand;
    private String cardHolder;
    private String expirationYear;
    private String expirationMonth;
    private String issuerBank;
    private String country;
    private String accountFundingSource;
    private String cardId;
}
