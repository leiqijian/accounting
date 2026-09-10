package com.liquido.worker.pojo.bo;

import java.io.Serializable;

import com.liquido.base.enums.DictionaryTypeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryDictValueBo implements Serializable {

    private static final long serialVersionUID = 1L;

    private DictionaryTypeEnum dictionaryType;

    private String key;

    private String[] subTypes;
}
