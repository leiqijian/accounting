package com.liquido.base.pojo.bo;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountMaxMonthBo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long accountId;

    private Integer activeMonth;

}
