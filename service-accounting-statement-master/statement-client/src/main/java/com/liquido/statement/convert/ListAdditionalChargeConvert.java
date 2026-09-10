package com.liquido.statement.convert;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.liquido.core.common.utils.JsonUtil;
import com.liquido.statement.pojo.bo.AdditionalCharge;

import com.fasterxml.jackson.core.type.TypeReference;

@Converter
public class ListAdditionalChargeConvert
        implements AttributeConverter<List<AdditionalCharge>, String> {

    @Override
    public String convertToDatabaseColumn(final List<AdditionalCharge> dataList) {
        return JsonUtil.toJson(Optional.ofNullable(dataList).orElse(Collections.emptyList()));
    }

    @Override
    public List<AdditionalCharge> convertToEntityAttribute(final String dbData) {

        final List<AdditionalCharge> result = JsonUtil.toBean(dbData, new TypeReference<>() {
        });

        return Optional.ofNullable(result).orElse(Collections.emptyList());
    }

}
