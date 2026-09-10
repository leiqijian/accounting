package com.liquido.aqueducts.vo.payout;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PixPaymentInfo {
    private String pixKey;
    private String pixKeyType;
    private String pixQrCodeContent;
    private String pixEndToEndId;
    private String fullNameFromPixKey;
    private String documentIdFromPixKey;

}
