package com.liquido.base.service.impl.lark;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import com.liquido.base.common.Constant.LARK;
import com.liquido.base.common.properties.LarkProperties;
import com.liquido.base.enums.LarkRemindRankEnum;
import com.liquido.base.exception.BaseExceptionCode;
import com.liquido.base.feign.LarkMessageFeign;
import com.liquido.base.pojo.bo.LarkBathMessageBo;
import com.liquido.base.pojo.bo.LarkMessageBo;
import com.liquido.base.pojo.dto.LarkBathMessageDto;
import com.liquido.base.pojo.dto.LarkMessageDto;
import com.liquido.base.pojo.dto.LarkResponseDto;
import com.liquido.base.pojo.dto.LarkTenantAccessTokenDto;
import com.liquido.base.pojo.vo.lark.LarkBathMessageVo;
import com.liquido.base.pojo.vo.lark.LarkMessageVo;
import com.liquido.base.service.LarkMessageService;
import com.liquido.base.service.monitor.LarkRobotTemplate;
import com.liquido.core.common.cache.RedisCacheUtil;
import com.liquido.core.common.utils.JsonUtil;
import com.liquido.core.common.utils.LocalDateTimeUtil;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class LarkMessageServiceImpl implements LarkMessageService {

    private final LarkMessageFeign larkMessageFeign;

    private final RedisCacheUtil redisCacheUtil;

    private final LarkProperties.Auth authProperties;

    private static final String BEARER = "Bearer";

    private static final String INTERACTIVE_MESSAGE_TYPE = "interactive";

    public static final String ROBOT_EMERGENCY_TPL = "message_emergency_tpl.json";

    private static final int LARK_SUCCESS_CODE = 0;

    private String getLarkTenantAccessToken(final String appId, final String appSecret) {

        final String cacheKey = Optional.ofNullable(appId)
                .map(v -> String.format("%s-%s", appId, LARK.ACCESS_TOKEN))
                .orElse(LARK.ACCESS_TOKEN);

        final String larkAccessToken = redisCacheUtil.getCacheObject(cacheKey);

        if (Objects.nonNull(larkAccessToken)) {
            return larkAccessToken;
        }
        final LarkTenantAccessTokenDto tenantAccessToken =
                larkMessageFeign.getTenantAccessToken(
                        Optional.ofNullable(appId).orElse(authProperties.getAppId()),
                        Optional.ofNullable(appSecret).orElse(authProperties.getAppSecret()));

        final String token =
                String.format("%s %s", BEARER, tenantAccessToken.getTenantAccessToken());

        redisCacheUtil.setCacheObject(cacheKey, token, 1800, TimeUnit.SECONDS);

        return token;
    }

    @Override
    public void sendLarkMessage(final LarkMessageVo vo) {

        final LarkMessageBo bo = LarkMessageBo
                .builder()
                .receiveId(vo.getReceiveId())
                .msgType(INTERACTIVE_MESSAGE_TYPE)
                // send a single message card, need escape
                .content(buildMessageContent(vo.getRemindRank(),
                        vo.getTitle(), vo.getContent(), vo.getRemark()))
                .build();

        log.info("send message param: {}", bo);
        final LarkResponseDto<LarkMessageDto> dto = larkMessageFeign
                .sendMessageToLarkUser(getLarkTenantAccessToken(vo.getAppId(), vo.getAppSecret()),
                        "user_id", bo);

        if (LARK_SUCCESS_CODE != dto.getCode()) {
            log.error("send message to lark user fail msg:{}", dto.getMsg());
            throw BaseExceptionCode.SEND_MESSAGE_TO_LARK_USER_FAIL.exception(dto.getMsg());
        }
    }

    @Override
    public void bathSendLarkMessage(final LarkBathMessageVo vo) {
        final LarkBathMessageBo bo = LarkBathMessageBo.builder().userIds(vo.getReceiveIds())
                .msgType(INTERACTIVE_MESSAGE_TYPE)
                // bath send message card, don't need escape
                .card(JsonUtil.toBean(buildMessageContent(vo.getRemindRank(),
                        vo.getTitle(), vo.getContent(), vo.getRemark()), JsonNode.class))
                .build();

        log.info("bath send lark message param :{}", bo);
        final LarkResponseDto<LarkBathMessageDto> dto =
                larkMessageFeign.bathSendMessageToLarkUser(
                        getLarkTenantAccessToken(vo.getAppId(), vo.getAppSecret()), bo);

        if (LARK_SUCCESS_CODE != dto.getCode()) {
            log.error("bath send message to lark user fail msg:{}", dto.getMsg());
            throw BaseExceptionCode.BATH_SEND_MESSAGE_TO_LARK_USER_FAIL.exception(dto.getMsg());
        }
    }

    @Override
    public void bathSendListLarkMessage(final Collection<LarkBathMessageVo> list) {
        list.forEach(this::bathSendLarkMessage);
    }

    private String buildMessageContent(final LarkRemindRankEnum remindRank, final String title,
            final String content, final String remark) {

        final String timeNow = Instant.now().atZone(ZoneOffset.UTC).toLocalDateTime()
                .format(LocalDateTimeUtil.FORMAT_DATETIME);

        final String template = LarkRobotTemplate.getInstance()
                .getLarkTemplate(ROBOT_EMERGENCY_TPL);

        return new StringSubstitutor(
                Map.of("headColor", remindRank.getColor(),
                        "title", "[" + remindRank.getCode() + "] " + title,
                        "content", Optional.ofNullable(content).orElse(""),
                        "timeNow", timeNow,
                        "remark", StringEscapeUtils.escapeJson(Optional.ofNullable(remark)
                                .orElse("")))).replace(template);
    }
}
