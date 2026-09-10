package com.liquido.base.pojo.vo;

import java.util.List;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class EmailMessageVo extends EmailBaseVo {

    private static final long serialVersionUID = 1L;

    @NotNull
    private List<String> to;

    private List<String> cc;

    @NotNull
    private String subject;

    @NotNull
    private String content;

}
