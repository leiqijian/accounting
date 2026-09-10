package com.liquido.aqueducts.vo.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubAccountInfo {
    private String accountId;
    private String accountName;
    private long createTimestamp;
}
