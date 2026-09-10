package com.liquido.base.pojo.vo;

import java.util.List;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class EmailAttachmentMessageVo extends EmailMessageVo {

    private static final long serialVersionUID = 1L;

    /**
     * html or text
     */
    @NotNull
    private Boolean html;

    private String[] pathToAttachment;

    private List<FileBytesVo> fileBytesList;

}
