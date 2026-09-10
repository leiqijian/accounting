package com.liquido.statement.pojo.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.liquido.core.common.utils.JsonUtil;
import com.liquido.statement.pojo.bo.AccountConfigData;
import com.liquido.statement.pojo.bo.ExchangeRateConfig;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Type;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountConfigDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private Long merchantId;

    @Type(type = "json")
    private ExchangeRateConfig exchangeRateConfig;

    @Type(type = "json")
    private AccountConfigData configData;

    private Long version;

    @Converter
    public static class Convert implements AttributeConverter<AccountConfigDto, String> {

        @SneakyThrows
        @Override
        public String convertToDatabaseColumn(final AccountConfigDto dto) {
            return Objects.isNull(dto) ? null : JsonUtil.toJson(dto);
        }

        @SneakyThrows
        @Override
        public AccountConfigDto convertToEntityAttribute(final String dbValue) {
            return StringUtils.isBlank(dbValue)
                    ? null : JsonUtil.toBean(dbValue, new TypeReference<>() {
            });
        }
    }
}
