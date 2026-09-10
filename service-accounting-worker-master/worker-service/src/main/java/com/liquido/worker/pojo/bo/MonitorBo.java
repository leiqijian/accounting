package com.liquido.worker.pojo.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonitorBo {

    private String title;

    private String merchant;

    private String country;

    private String transactionType;

    private String uniqueId;

    private String msgInfo;

    private String detail;

}
