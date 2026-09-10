package com.liquido.aqueducts.vo.response;

import lombok.*;

/**
 * By convention, different data in different productCode return results are wrapped in an object
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Information {
    private String referenceCode;
}
