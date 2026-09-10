package com.liquido.base.pojo.dto;


import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.base.enums.VendorCodeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CostCardConfigInitDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Integer> activeVersionList;

    private Map<String, Long> merchantCodeIdMap;

    private Map<CountryCodeEnum, List<VendorCodeEnum>> countryVendorDtoList;

}
