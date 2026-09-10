package com.liquido.statement.service.monitor.lark;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.Maps;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.ResourceUtils;

@Slf4j
public class LarkRobotTemplate {
    private static final String TEMPLATE_PATH = "templates";

    public static final String ROBOT_EMERGENCY_TPL = "robot_emergency_tpl.json";

    private static final Map<String, String> templateMap = Maps.newConcurrentMap();

    static {
        try {
            final Resource[] resources = new PathMatchingResourcePatternResolver()
                    .getResources(ResourceUtils.CLASSPATH_URL_PREFIX + "templates/lark/**/*.json");
            if (resources != null && resources.length >= 0) {
                for (final Resource resource : resources) {
                    templateMap.put(resource.getFilename(), getFileContent(resource));
                }
            }
        } catch (IOException e) {
            log.error("init lark robot template error:{}", e.getMessage());
        }
    }

    private LarkRobotTemplate() {
    }

    private static LarkRobotTemplate instance;

    public static LarkRobotTemplate getInstance() {
        if (instance == null) {
            synchronized (LarkRobotTemplate.class) {
                if (instance == null) {
                    instance = new LarkRobotTemplate();
                }
            }
        }
        return instance;
    }

    private static String getTemplate(final String templateName) {
        if (StringUtils.isBlank(templateName)) {
            return StringUtils.EMPTY;
        }
        return getFileContent(new ClassPathResource(TEMPLATE_PATH + File.separator + templateName));
    }

    private static String getFileContent(final Resource resource) {
        try {
            @Cleanup InputStreamReader isr = new InputStreamReader(resource.getInputStream());
            @Cleanup BufferedReader reader = new BufferedReader(isr);
            return StringUtils.defaultIfBlank(IOUtils.readLines(reader).stream()
                    .map(String::trim).collect(Collectors.joining()), "{}");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return StringUtils.EMPTY;
    }

    public String getLarkTemplate(final String templateName) {
        if (StringUtils.isBlank(templateName)) {
            return StringUtils.EMPTY;
        }

        if (StringUtils.isNotBlank(templateMap.get(templateName))) {
            return templateMap.get(templateName);
        }

        final String templateContent = getTemplate(templateName);
        templateMap.put(templateName, templateContent);
        return templateContent;
    }
}
