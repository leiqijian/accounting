package com.liquido.statement.pojo.vo;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListAccountConfigVo implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long merchantId;

    private Long id;

    private List<Long> ids;

}
