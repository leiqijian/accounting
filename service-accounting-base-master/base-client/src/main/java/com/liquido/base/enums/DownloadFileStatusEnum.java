package com.liquido.base.enums;

import java.util.Arrays;
import java.util.Objects;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.liquido.core.common.exception.CommonExceptionCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DownloadFileStatusEnum {

    PENDING("PENDING", "PENDING"),

    IN_PROGRESS("IN_PROGRESS", "IN_PROGRESS"),

    COMPLETE("COMPLETE", "COMPLETE"),

    FAIL("FAIL", "FAIL"),

    ;

    private final String code;
    private final String remark;

    public static DownloadFileStatusEnum parse(final String code) {
        if (Objects.isNull(code)) {
            return null;
        }
        return Arrays.stream(DownloadFileStatusEnum.values())
                .filter(tmp -> tmp.getCode().equals(code))
                .findFirst()
                .orElse(null);
    }

    @Converter
    public static class Convert implements AttributeConverter<DownloadFileStatusEnum, String> {

        @Override
        public String convertToDatabaseColumn(final DownloadFileStatusEnum enumValue) {
            if (Objects.isNull(enumValue)) {
                throw CommonExceptionCode.PARAMETER_MISSING.exception("downloadFileStatusEnum");
            }
            return enumValue.getCode();
        }

        @Override
        public DownloadFileStatusEnum convertToEntityAttribute(final String dbValue) {
            return DownloadFileStatusEnum.parse(dbValue);
        }
    }
}
