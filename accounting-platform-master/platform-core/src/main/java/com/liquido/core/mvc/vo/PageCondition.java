package com.liquido.core.mvc.vo;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Convert;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.liquido.core.common.validator.ColumnField;
import com.liquido.core.mvc.enums.SortTypeEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PageCondition implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * start index:1
     */
    @NotNull(message = "pageNo required")
    @Min(0)
    private Integer pageNo;

    @NotNull(message = "pageSize required")
    @Min(1)
    @Max(10000)
    private Integer pageSize;

    @ColumnField(message = "sortField name invalid")
    private String sortField;

    @Convert(converter = SortTypeEnum.Convert.class)
    private SortTypeEnum sortType;

    public Integer getOffset() {

        if (Objects.isNull(this.getPageNo()) || this.getPageNo() < 1) {
            this.pageNo = 1;
        }

        if (Objects.isNull(this.getPageSize()) || this.getPageSize() < 1) {
            this.pageSize = 20;
        }

        return (this.getPageNo() - 1) * this.getPageSize();
    }
}
