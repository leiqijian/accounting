package com.liquido.statement.pojo.vo;

import java.io.Serializable;

import com.liquido.core.mvc.vo.PageCondition;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class PageAccountConfigVo extends PageCondition implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long merchantId;

    private Long id;

}
