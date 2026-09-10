package com.liquido.base.feign;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

import com.liquido.core.common.feign.Decoder;
import com.liquido.core.common.utils.CheckResponseUtil;
import com.liquido.core.common.utils.JsonUtil;
import com.liquido.core.mvc.dto.ResponseDto;

import com.fasterxml.jackson.databind.JavaType;
import feign.Response;
import feign.Util;

public class QueryDictAllValueDecoder implements Decoder {

    @Override
    public Object decode(final Response response, final Type type) throws IOException {
        final Class<?> classType = QueryDictAllValueAspect.getClassType();
        final String content = Util.toString(response.body().asReader(Util.UTF_8));
        final JavaType map = JsonUtil
                .constructParametricType(Map.class, String.class, String.class);
        final JavaType javaType = JsonUtil.constructParametricType(ResponseDto.class, map);
        final ResponseDto<Map<String, Object>> vo = JsonUtil.toBean(content, javaType);
        CheckResponseUtil.checkResponse(vo);
        vo.getData().replaceAll((k, v) -> JsonUtil.toBean((String) v, classType));
        return vo.getData();
    }
}
