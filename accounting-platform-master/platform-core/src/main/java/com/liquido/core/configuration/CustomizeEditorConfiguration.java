package com.liquido.core.configuration;

import java.beans.PropertyEditorSupport;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.core.common.utils.LocalDateUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

@Configuration
@ConditionalOnWebApplication
@ConditionalOnClass(RequestMappingHandlerAdapter.class)
public class CustomizeEditorConfiguration {

    @Autowired
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public void setWebBindingInitializer(
            final RequestMappingHandlerAdapter requestMappingHandlerAdapter) {
        requestMappingHandlerAdapter.setWebBindingInitializer(binder -> {
            binder.registerCustomEditor(LocalDate.class, new PropertyEditorSupport() {

                @Override
                public String getAsText() {
                    return ((LocalDate) getValue()).format(LocalDateUtil.FORMAT_DATE);
                }

                @Override
                public void setAsText(String text) throws IllegalArgumentException {
                    setValue(LocalDateUtil.formatToLocalDate(text));
                }

            });

            binder.registerCustomEditor(LocalDateTime.class, new PropertyEditorSupport() {

                @Override
                public String getAsText() {
                    return ((LocalDateTime) getValue()).format(LocalDateTimeUtil.FORMAT_DATETIME);
                }

                @Override
                public void setAsText(String text) throws IllegalArgumentException {
                    setValue(LocalDateTimeUtil.formatToLocalDateTime(text));
                }

            });
        });
    }

}
