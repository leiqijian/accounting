package com.liquido.aqueducts.vo.payout;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentInfo {
    private PixPaymentInfo pix;
    private BankTransferPaymentInfo bankTransfer;
}
