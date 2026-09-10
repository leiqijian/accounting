package com.liquido.core.common.convert;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.liquido.core.common.exception.CommonExceptionCode;
import com.liquido.core.common.security.AesUtil;

@Converter
public class AesEncryptorConverter implements AttributeConverter<String, String> {

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return attribute;
        }
        try {
            return AesUtil.encrypt(attribute);
        } catch (Exception e) {
            throw CommonExceptionCode.DECRYPTION_FAIL.exception("AES");
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return dbData;
        }
        try {
            return AesUtil.decrypt(dbData);
        } catch (Exception e) {
            throw CommonExceptionCode.DECRYPTION_FAIL.exception("AES");
        }
    }
}
