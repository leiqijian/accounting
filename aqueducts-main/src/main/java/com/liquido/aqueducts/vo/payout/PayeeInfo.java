package com.liquido.aqueducts.vo.payout;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayeeInfo {
    private String targetName;

    private String  targetLastName;

    private String  targetEmail;

    private String  targetDocumentId;

    private String targetDocumentType;

    private String  targetBirthDate;

    private String  targetPhone;
}
