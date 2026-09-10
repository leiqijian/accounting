package com.liquido.aqueducts.config.source;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SourceNode {
    private boolean ignore;
    private String collection;
    private String db;
    private String tablePrefix;
    private String uniqueId;
    private String productType;
}
