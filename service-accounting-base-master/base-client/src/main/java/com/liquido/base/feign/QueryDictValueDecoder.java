package com.liquido.base.feign;

import java.io.IOException;
import java.lang.reflect.Type;

import com.liquido.core.common.feign.Decoder;
import com.liquido.core.common.utils.CheckResponseUtil;
import com.liquido.core.common.utils.JsonUtil;
import com.liquido.core.mvc.dto.ResponseDto;

import com.fasterxml.jackson.databind.JavaType;
import feign.Response;
import feign.Util;

public class QueryDictValueDecoder implements Decoder {

    @Override
    public Object decode(final Response response,
                         final Type type) throws IOException {
        final Class<?> classType = QueryDictValueAspect.getClassType();
        final String content = Util.toString(response.body().asReader(Util.UTF_8));
        final JavaType javaType = JsonUtil.constructParametricType(ResponseDto.class, String.class);
        final ResponseDto<Object> vo = JsonUtil.toBean(content, javaType);
        CheckResponseUtil.checkResponse(vo);
        vo.setData(JsonUtil.toBean((String) vo.getData(), classType));
        return vo.getData();
    }
}
