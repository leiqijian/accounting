package com.liquido.worker.pojo.bo;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Data Warehouse Response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DwResponse<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer code;

    private String message;

    private T data;

    public Boolean isSuccess() {
        return this.code.compareTo(0) == 0;
    }

}
