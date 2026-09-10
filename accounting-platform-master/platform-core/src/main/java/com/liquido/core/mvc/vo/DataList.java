package com.liquido.core.mvc.vo;

import java.io.Serializable;
import java.util.Collection;

import lombok.Data;

/**
 * wrap the response message body of the collection class
 */
@Data
public class DataList<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private Collection<T> dataList;

    public DataList() {
    }

    public DataList(final Collection<T> dataList) {
        this.dataList = dataList;
    }
}
