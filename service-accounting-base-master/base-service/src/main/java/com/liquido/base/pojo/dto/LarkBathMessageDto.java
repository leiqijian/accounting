package com.liquido.base.pojo.dto;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LarkBathMessageDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("message_id")
    private String messageId;

    @JsonProperty("invalid_department_ids")
    private List invalidDepartmentIds;

    @JsonProperty("invalid_open_ids")
    private List invalidOpenIds;

    @JsonProperty("invalid_user_ids")
    private List invalidUserIds;

    @JsonProperty("invalid_union_ids")
    private List invalidUnionIds;
}
