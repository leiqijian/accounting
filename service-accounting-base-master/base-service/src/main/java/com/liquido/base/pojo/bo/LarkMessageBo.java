package com.liquido.base.pojo.bo;

import java.io.Serializable;
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
public class LarkMessageBo implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * lark openId or lark userId
     */
    @NotBlank
    @JsonProperty("receive_id")
    private String receiveId;

    /**
     * text; post; image; file; audio; media; interactive...
     */
    @NotBlank
    @JsonProperty("msg_type")
    private String msgType;

    /**
     * JSON structure serialized string
     * 1. The JSON string needs to be escaped. For example, the newline character is
     *    after being escaped.
     * 2. The maximum text message request body cannot exceed 150KB
     * 3. The maximum request body for cards and rich text messages cannot exceed 30KB.
     */
    @NotBlank
    private String content;

    private String uuid;
}
