package com.liquido.core.configuration;


import java.util.List;
import java.util.Map;

import com.liquido.core.mvc.vo.AccessPartnerVo;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Common Properties
 */
@Data
@Component
@ConfigurationProperties(prefix = CommonProperties.COMMON_PREFIX)
public class CommonProperties {

    public static final String COMMON_PREFIX = "ms.common";

    private String logSwitch = "off";

    private MvcConfig mvc;

    private List<LogSens> logSens;

    private SnowFlake snowFlake;

    private SwaggerConfig swagger;

    private CacheService cacheService;

    public enum SnowflakeModeEnum {
        HOST,
        IPV4,
        CONFIG
    }

    @Data
    public static class CacheService {

        private Boolean enable = false;

        private Caffeine caffeine = new Caffeine();
    }

    @Data
    public static class Caffeine {

        private Integer maximumSize = 500;
    }

    @Data
    public static class MvcConfig {
        // Whether the current microservice as a whole is a REST service,
        // true: the response is a JSON structure,
        // If the value is false: the program will still dynamically determine
        // whether the currently requested service is Rest, if it is Rest,
        // the value is changed to true, otherwise it means that there is no ModelAndView view
        // see AbstractGlobalHandlerExceptionResolver.doResolveException() method
        // Default: true is Rest
        private Boolean restService = true;

        // inner service default response desensitization=true
        private Boolean desensitization = false;

        private Signature signature;
    }

    @Data
    public static class Signature {

        private Boolean enable;

        private Map<String, AccessPartnerVo> assessPartner;
    }

    @Data
    public static class SnowFlake {

        private SnowflakeModeEnum mode;

        /**
         * dataCenter id
         * It can be understood as the number of computer rooms. The value cannot exceed 7.
         * Starting from 0, it can be expanded to 8 computer rooms at most.
         */
        private Integer centerId = 0;

        /**
         * workerId
         * It can be understood as the number of machines. The value is not available beyond 1023.
         * It starts from 0 and can be expanded to the 1024th machine at most.
         */
        private Integer workerId = 0;

        /**
         * Whether the snowflake algorithm is generated in the K8S environment
         */
        private boolean k8s = false;
    }

    @Data
    public static class LogSens {
        private Class<?> excludeClass;
    }

    @Data
    public static class SwaggerConfig {

        /**
         * swagger Scan the specified controller package, multiple packages are separated by commas
         */
        private String basePackage;

        /**
         * swagger Generate custom document titles
         */
        private String apiTitle;

        /**
         * swagger Generate custom document descriptions
         */
        private String apiDescription;

        /**
         * swagger Generate custom documentation versions
         */
        private String apiVersion;

        /**
         * Document Contact
         */
        private String apiContactName;
    }
}
