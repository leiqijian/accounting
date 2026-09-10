package com.liquido.worker.pojo.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("PMD.TooManyFields")
public class DwQueryTransactionDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String uniqueId;

    private String merchantReference;

    private String currency;

    private BigDecimal amount;

    private String status;

    private String transferErrorMsg;

    private Long createTime;

    private JsonNode lifeCycle;

    private JsonNode card;

    private JsonNode subAccount;

    private JsonNode account;

    private JsonNode payer;

    private JsonNode beneficiary;

    private JsonNode skuDetail;

    private ObjectNode pixCredentials;

    private JsonNode information;

    private ObjectNode cepCredentials;

    private ObjectNode refundAccountInfo;

    private JsonNode others;

    private JsonNode transferDetail;

}
