package com.liquido.statement.pojo.vo;


import java.io.Serializable;
import java.util.Objects;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.liquido.core.common.utils.JsonUtil;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionBizExtendInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String thirdPartyOrderId;

    private String oppositeName;


    @Converter
    public static class Convert implements AttributeConverter<TransactionBizExtendInfo, String> {

        @SneakyThrows
        @Override
        public String convertToDatabaseColumn(final TransactionBizExtendInfo ext) {
            return Objects.isNull(ext) ? null : JsonUtil.toJson(ext);
        }

        @SneakyThrows
        @Override
        public TransactionBizExtendInfo convertToEntityAttribute(final String dbValue) {
            return StringUtils.isBlank(dbValue)
                    ? null : JsonUtil.toBean(dbValue, TransactionBizExtendInfo.class);
        }
    }
}
