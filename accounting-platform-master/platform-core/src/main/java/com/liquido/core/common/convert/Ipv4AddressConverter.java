package com.liquido.core.common.convert;

import java.util.Objects;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.liquido.core.common.utils.WebUtil;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
@Converter
public class Ipv4AddressConverter implements AttributeConverter<String, Long> {

    @Override
    public Long convertToDatabaseColumn(final String attribute) {
        if (StringUtils.isBlank(attribute)) {
            return 0L;
        }

        return WebUtil.inetAton(attribute);
    }

    @Override
    public String convertToEntityAttribute(Long dbData) {
        if (Objects.isNull(dbData) || dbData <= 0) {
            return "";
        }

        try {
            return WebUtil.inetNtoa(dbData);
        } catch (Exception e) {
            log.error("convert to entity attribute error", e);
        }

        return "";
    }
}
