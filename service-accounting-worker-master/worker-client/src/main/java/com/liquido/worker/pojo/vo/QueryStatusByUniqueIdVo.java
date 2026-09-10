package com.liquido.worker.pojo.vo;

import java.io.Serializable;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryStatusByUniqueIdVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @NotEmpty
    private List<QueryStatusVo> queries;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QueryStatusVo {

        @NotNull
        private Long workOrderId;

        @NotBlank
        private String uniqueId;

        @NotBlank
        private String merchantCode;

    }

}
