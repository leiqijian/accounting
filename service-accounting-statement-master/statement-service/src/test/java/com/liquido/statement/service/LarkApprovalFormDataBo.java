package com.liquido.statement.service;

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
public class LarkApprovalFormDataBo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;

    private String name;

    private String type;

    private Object value;

    private List<Option> option;

    private String ext;
    @Data
    public static class Option {

        private String key;

        private String text;
    }
}
