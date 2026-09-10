package com.liquido.transaction.pojo.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdditionalInfoConfigDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String key;

    private String showKey;

}
