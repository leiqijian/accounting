package com.liquido.core.common.cache;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import com.liquido.core.common.exception.CommonExceptionCode;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * CacheStringSerializer
 */
@Setter
@Getter
public class CacheStringSerializer extends StringRedisSerializer {

    private static final Charset charset = StandardCharsets.UTF_8;
    private static final String DELIMITER = ".";
    private String prefix;
    private int maxKeyLength;

    public CacheStringSerializer() {
        this.prefix = "";
        this.maxKeyLength = 255;
    }

    public CacheStringSerializer(final String prefix, final int maxKeyLength) {
        this.prefix = prefix;
        this.maxKeyLength = maxKeyLength;
    }

    @Override
    public String deserialize(final byte[] bytes) {
        return super.deserialize(bytes);
    }

    @Override
    public byte[] serialize(final String string) {
        return super.serialize(generateKey(string));
    }

    public String generateKey(final String key) {
        if (StringUtils.isBlank(key)) {
            throw CommonExceptionCode.PARAMETER_ILLEGAL_BLANK.exception();
        }
        return StringUtils.isBlank(prefix) ? key : prefix + DELIMITER + key;
    }
}
