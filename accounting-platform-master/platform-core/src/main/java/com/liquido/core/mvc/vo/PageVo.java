package com.liquido.core.mvc.vo;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import lombok.Data;
import org.springframework.util.CollectionUtils;

/**
 * PageVo
 */
@Data
public class PageVo<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer pageNo;
    private Integer pageSize;
    private Integer pageCount;
    private Boolean hasNext;
    private Boolean hasPrev;

    // total count
    private Long total;

    // data content list
    private List<T> dataList;

    // subtotal list data
    private Object subtotal;

    public PageVo() {
    }

    public PageVo(final int pageNo,
                  final int pageSize,
                  final long total,
                  final List<T> dataList) {

        this.pageNo = pageNo <= 1 ? 1 : pageNo;
        this.pageSize = pageSize <= 1 ? 1 : pageSize;
        this.total = total <= 0 ? 0 : total;
        this.dataList = CollectionUtils.isEmpty(dataList) ? Collections.emptyList() : dataList;
    }

    public PageVo(final int pageNo,
                  final int pageSize,
                  final long total,
                  final List<T> dataList,
                  final Object subtotal) {

        this.pageNo = pageNo <= 1 ? 1 : pageNo;
        this.pageSize = pageSize <= 1 ? 1 : pageSize;
        this.total = total <= 0 ? 0 : total;
        this.dataList = CollectionUtils.isEmpty(dataList) ? Collections.emptyList() : dataList;
        this.subtotal = subtotal;
    }

    public static <T> PageVo<T> buildEmptyPage(final int pageSize) {
        return new PageVo(1, pageSize, 0L, Collections.emptyList());
    }

    public int getPageCount() {
        return (int) Math.ceil((double) total / (double) pageSize);
    }

    public Boolean getHasNext() {
        return pageNo >= 1 && pageNo < getPageCount();
    }

    public Boolean getHasPrev() {
        return pageNo > 1 && pageNo <= getPageCount();
    }

    public int getStartIndex() {
        return (pageNo - 1) * pageSize;
    }

    public long getLastIndex() {
        return (pageNo + 1) * pageSize > total ? total : (pageNo + 1) * pageSize;
    }
}
