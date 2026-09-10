package com.liquido.statement.pojo.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GlobalAccountInfoDto implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Whether to open global account
     * true: opened
     * false: Not opened
     */
    private Boolean openFlag;

    /**
     * when openFlag = ture
     * return globalAccount info
     */
    private GlobalAccountDto globalAccount;
}
