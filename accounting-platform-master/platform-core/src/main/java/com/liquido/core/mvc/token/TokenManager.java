package com.liquido.core.mvc.token;

import java.util.List;
import javax.annotation.Resource;

import com.liquido.core.common.cache.RedisDistLock;
import com.liquido.core.common.utils.DataUtil;
import com.liquido.core.mvc.dto.ResponseDto;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * Token Manager
 */
public class TokenManager {

    @Resource
    private RedisDistLock redisLock;

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * token
     */
    public static final String TOKEN_NAME = "token";
    /**
     * token
     */
    public static final String TOKEN_STR = "token-str";
    /**
     * token prefix
     */
    public static final String TOKEN_PREFIX = "token_";
    /**
     * default expire time second
     */
    public static final int DEFAULT_EXPIRE = 180;
    /**
     * retry times
     */
    public static final int DEFAULT_RETRY_TIMES = 3;
    /**
     * default expire time second in cache
     */
    public static int expire = DEFAULT_EXPIRE;

    /**
     * put token to cache retry times
     */
    private int retryTimes = DEFAULT_RETRY_TIMES;

    private List<String> sucCodeList;


    public String newToken() {
        String token = null;
        for (int i = 0; i < retryTimes; i++) {
            if (token != null) {
                break;
            }

            token = DataUtil.getUuid();

            boolean flag = redisLock.getLock(TOKEN_PREFIX, token, 5000);
            if (!flag) {
                token = getToken(token);
            }
        }
        if (null == token) {
            token = DataUtil.getUuid();
        }
        return token;
    }

    public boolean checkToken(final String token) {

        final Object realToken = getToken(TOKEN_PREFIX + token);

        return realToken == null ? false : realToken.equals(TOKEN_STR);
    }

    public String getToken(final String token) {
        final Object value = redisTemplate.opsForValue().get(TOKEN_PREFIX + token);
        if (value == null) {
            return null;
        }
        return value.toString();
    }

    public boolean checkAndDelToken(final String token) {
        if (checkToken(token)) {
            redisTemplate.delete(TOKEN_PREFIX + token);
        }
        return false;
    }

    public List<String> getSucCodeList() {
        return sucCodeList;
    }

    @Value("#{globalProp[sucCodeList]}")
    public void setSucCodeList(String sucCodeList) {
        if (StringUtils.isBlank(sucCodeList)) {
            this.sucCodeList = Lists.newArrayList(String.valueOf(ResponseDto.SUCCESS_CODE));
        } else {
            this.sucCodeList = Lists.newArrayList(Splitter.on(",").split(sucCodeList));
        }
    }
}
