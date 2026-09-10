package com.liquido.statement.pojo.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductsTransactionCountDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<String> dimensions;

    private List<Source> sourceList;

    @Data
    public static class Source implements Serializable {
        private static final long serialVersionUID = 4090733347813989462L;

        /**
         * x-axis, representing dates
         */
        private LocalDate product;

        /**
         * transactions count
         */
        private Long payIn;

        /**
         * transactions count
         */
        private Long payOut;

        /**
         * transactions count
         */
        private Long marketPlace;
    }

}
