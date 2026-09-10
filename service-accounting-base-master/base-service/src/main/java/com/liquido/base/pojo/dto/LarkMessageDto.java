package com.liquido.base.pojo.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LarkMessageDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("message_id")
    private String messageId;

    @JsonProperty("root_id")
    private String rootId;

    @JsonProperty("parent_id")
    private String parentId;

    @JsonProperty("msg_type")
    private String msgType;

    @JsonProperty("create_time")
    private String createTime;

    @JsonProperty("update_time")
    private String updateTime;

    private Boolean deleted;

    private Boolean updated;

    @JsonProperty("chat_id")
    private String chatId;

    private Sender sender;

    private Body body;

    private Mentions mentions;

    @JsonProperty("upper_message_id")
    private String upperMessageId;

    @Data
    static class Sender {

        private String id;

        @JsonProperty("id_type")
        private String idType;

        @JsonProperty("sender_type")
        private String senderType;

        @JsonProperty("tenant_key")
        private String tenantKey;
    }

    @Data
    static class Body {

        private String content;
    }

    @Data
    static class Mentions {

        private String key;

        private String id;

        @JsonProperty("id_type")
        private String idType;

        private String name;

        @JsonProperty("tenant_key")
        private String tenantKey;
    }
}
