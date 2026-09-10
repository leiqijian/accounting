package com.liquido.aqueducts.vo.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayerInfo {
    private String reference;
    private String ip;
    private String name;
    private String documentId;
    private String documentType;
    private String phone;
    private String email;
    private String description;
    private String orderId;
}
