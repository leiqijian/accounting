package com.liquido.worker.pojo.vo;

import java.io.Serializable;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchQueryPaymentLinkVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * merchant's order id
     */
    @NotEmpty
    private List<String> merchantReference;

    @NotBlank
    private String merchantCode;

}
