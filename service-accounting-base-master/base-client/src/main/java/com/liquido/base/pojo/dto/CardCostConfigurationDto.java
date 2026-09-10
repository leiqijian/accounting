package com.liquido.base.pojo.dto;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardCostConfigurationDto implements Serializable {

    private static final long serialVersionUID = 1L;

    // card(credit card, debit card) monthly cost configuration
    private List<CardCostConfigDto> cardCostConfigList;

}
