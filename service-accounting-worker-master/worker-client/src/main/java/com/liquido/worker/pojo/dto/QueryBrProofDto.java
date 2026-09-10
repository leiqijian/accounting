package com.liquido.worker.pojo.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryBrProofDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String pixId;

    private String documentId;

    private String description;

    private String amount;

    private String name;

    private String date;

    private String time;

}
