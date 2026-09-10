package com.liquido.aqueducts.vo.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * transaction details response of payout pix + ted + spei
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PayoutDetail {
    private String uniqueId;
    private String merchantReference;
    private String status;
    private String tradeTransferStatus;
    private String tradeTransactionType;
    private String settleVendor;
    private String transferErrorMsg;
    private long createTime;
    private long amount;
    private String currency;
    private List<TransactionLifeCycle> lifeCycle;

    // beneficiary
    private PayoutBeneficiary beneficiary;

    // others
    private PayoutOthers others;

    /**
     * Pix Credentials Data
     */
    private PixCredentials pixCredentials;

    private CepCredentials cepCredentials;

}
