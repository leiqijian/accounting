package com.liquido.core.common.feign;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Optional;

import feign.FeignException;
import feign.MethodMetadata;
import feign.Request;
import feign.RequestTemplate;
import feign.Response;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.HttpMessageConverterCustomizer;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.context.ApplicationContext;

/**
 * @see FeignDecoder
 */
public class MethodFeignDecoder extends ResponseEntityDecoder {

    private final ApplicationContext context;

    public MethodFeignDecoder(final ApplicationContext context,
                              final ObjectFactory<HttpMessageConverters> messageConverters,
                              final ObjectProvider<HttpMessageConverterCustomizer> customizers) {
        super(new SpringDecoder(messageConverters, customizers));
        this.context = context;
    }

    @Override
    public Object decode(final Response response,
                         final Type type) throws IOException, FeignException {
        final FeignDecoder feignDecoder = Optional.ofNullable(response)
                .map(Response::request)
                .map(Request::requestTemplate)
                .map(RequestTemplate::methodMetadata)
                .map(MethodMetadata::method)
                .map(e -> e.getAnnotation(FeignDecoder.class))
                .orElse(null);

        if (Objects.nonNull(feignDecoder)) {
            Decoder decoder = context.getBean(feignDecoder.value());
            return decoder.decode(response, type);
        }
        return super.decode(response, type);
    }
}
