package com.liquido.base.pojo.vo;


import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailBaseVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String mailHost;

    private Integer mailPort;

    private String mailUsername;

    private String mailPassword;

}
