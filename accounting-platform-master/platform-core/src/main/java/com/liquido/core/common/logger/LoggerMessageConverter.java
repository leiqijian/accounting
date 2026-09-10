package com.liquido.core.common.logger;

import java.util.stream.Stream;

import ch.qos.logback.classic.pattern.MessageConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.helpers.MessageFormatter;

/**
 * Log output sensitive word desensitization processing tools
 * Configured at the top of the logback configuration file: <conversionRule conversionWord="msg"
 * converterClass="com.liquido.core.common.logger.LoggerMessageConverter"/>
 */
@Slf4j
public class LoggerMessageConverter extends MessageConverter {

    @Override
    public String convert(final ILoggingEvent event) {
        try {
            final Object[] args = event.getArgumentArray();
            if (args != null && args.length > 0) {
                return MessageFormatter.arrayFormat(event.getMessage(),
                        Stream.of(event.getArgumentArray())
                                .map(LogWrapper::convertMessage).toArray()).getMessage();
            }

            return super.convert(event);
        } catch (Exception e) {
            return super.convert(event);
        }
    }
}
