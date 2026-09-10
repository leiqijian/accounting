package com.liquido.worker.pojo.vo;

import java.io.Serializable;
import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryTransactionVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank
    private String uniqueId;

    private String merchantCode;

    public QueryTransactionVo(final String uniqueId) {
        this.uniqueId = uniqueId;
    }

}
