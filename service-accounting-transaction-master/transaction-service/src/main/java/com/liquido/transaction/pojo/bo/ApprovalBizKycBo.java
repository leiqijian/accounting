package com.liquido.transaction.pojo.bo;


import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApprovalBizKycBo {

    private Long id;

    private Long linkId;

    private Long approvalId;

    private String fullAccessLink;

    private LocalDateTime formSubmissionTime;

    private String merchantTradingName;

    private String merchantIncorporationCountry;

    private List<String> complianceOwners;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;

}
