package com.liquido.base.pojo.bo;

import java.io.Serializable;
import java.util.List;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LarkBathMessageBo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * text; post; image; file; audio; media; interactive...
     */
    @NotBlank
    @JsonProperty("msg_type")
    private String msgType;

    /**
     * The card and content fields must be chosen one from the other.
     */

    private Object content;

    private Object card;

    /**
     * Fill in at least one of the four fields department_ids, open_ids, user_ids, union_ids
     */
    @JsonProperty("department_ids")
    private List departmentIds;

    @JsonProperty("open_ids")
    private List openIds;

    @JsonProperty("user_ids")
    private List userIds;

    @JsonProperty("union_ids")
    private List unionIds;

}
