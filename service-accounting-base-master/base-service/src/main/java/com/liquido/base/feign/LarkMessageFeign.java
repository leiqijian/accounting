package com.liquido.base.feign;

import com.liquido.base.pojo.bo.LarkBathMessageBo;
import com.liquido.base.pojo.bo.LarkMessageBo;
import com.liquido.base.pojo.dto.LarkBathMessageDto;
import com.liquido.base.pojo.dto.LarkMessageDto;
import com.liquido.base.pojo.dto.LarkResponseDto;
import com.liquido.base.pojo.dto.LarkTenantAccessTokenDto;
import com.liquido.core.common.feign.CustomerRetryConfig;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "lark-message", url = "${base.lark.auth.url}",
        configuration = CustomerRetryConfig.class)
public interface LarkMessageFeign {

    String AUTHORIZATION = "Authorization";

    @PostMapping("/open-apis/auth/v3/tenant_access_token/internal")
    LarkTenantAccessTokenDto getTenantAccessToken(
            @RequestParam("app_id") String appId,
            @RequestParam("app_secret") String appSecret);

    @PostMapping("/open-apis/im/v1/messages")
    LarkResponseDto<LarkMessageDto> sendMessageToLarkUser(
            @RequestHeader(AUTHORIZATION) final String authorization,
            @RequestParam("receive_id_type") final String receiveIdType,
            @RequestBody final LarkMessageBo bo);

    @PostMapping("/open-apis/message/v4/batch_send/")
    LarkResponseDto<LarkBathMessageDto> bathSendMessageToLarkUser(
            @RequestHeader(AUTHORIZATION) final String authorization,
            @RequestBody final LarkBathMessageBo bo);
}
