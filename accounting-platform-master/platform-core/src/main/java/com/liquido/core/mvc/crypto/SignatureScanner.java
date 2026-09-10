package com.liquido.core.mvc.crypto;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.liquido.core.configuration.CommonProperties;
import com.liquido.core.mvc.vo.AccessPartnerVo;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.SmartLifecycle;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Signature Scanner
 */
@Slf4j
public class SignatureScanner implements SmartLifecycle {

    /**
     * Cache signature verify info corresponding to all RequestMapping URL addresses
     * key: API interface address, value: signature verify info
     */
    private static Map<String, SignatureVerify> candidateSignPathMap = Maps.newHashMap();
    /**
     * The algorithm implementation map corresponding to the signature policy algorithm
     */
    private static Map<SignAlgorithm, SignatureStrategy> providerStrategyMap = Maps.newHashMap();
    private final int phase = 1;
    private boolean isRunning = false;
    private final CommonProperties.Signature signature;

    @Resource
    private ApplicationContext context;

    public SignatureScanner(final List<SignatureStrategy> signatureStrategyList,
                            final CommonProperties.Signature signature) {
        this.signature = signature;
        if (!CollectionUtils.isEmpty(signatureStrategyList)) {
            providerStrategyMap = signatureStrategyList.stream().collect(
                    Collectors.toMap(SignatureStrategy::getAlgorithm, objSelf -> objSelf,
                            (k1, k2) -> k1));
            log.info("Initialize encryption algorithm signature implementation class collection:{}",
                    providerStrategyMap);
        }
    }

    @PostConstruct
    private void init() {
        final Map<String, SignatureStrategy> map = context.getBeansOfType(SignatureStrategy.class);
        map.forEach((key, value) -> providerStrategyMap.put(value.getAlgorithm(), value));

        log.info("Initialize the implementation class of the SignatureStrategy interface:{}",
                providerStrategyMap);
    }

    @Override
    public void start() {
        final Map<String, Object> beanMap = context.getBeansWithAnnotation(Controller.class);
        for (final Map.Entry<String, Object> entry : beanMap.entrySet()) {
            final Class<?> targetClass = this.getTargetClass(entry.getValue());
            if (Objects.nonNull(targetClass)) {
                this.scanCandidateSignController(targetClass);
            }
        }

        log.info("SignatureScanner.run finished! candidateSignPathMap:{}", candidateSignPathMap);
        isRunning = true;
    }

    public boolean needSignRequest(final String uri) {
        if (StringUtils.isNotBlank(uri) && candidateSignPathMap.containsKey(uri)) {
            return true;
        }

        return false;
    }

    /**
     * Obtain the corresponding signature algorithm according to the configured URL
     *
     * @param uri
     * @return
     */
    public SignatureStrategy getSignStrategy(final String uri) {
        return providerStrategyMap.get(candidateSignPathMap.get(uri).algorithm());
    }


    /**
     * Obtain the corresponding signature assess partner according to the configured URL
     *
     * @param uri
     * @return
     */
    public AccessPartnerVo getAssessPartner(final String uri) {
        return signature.getAssessPartner().get(candidateSignPathMap.get(uri).assessPartner());
    }

    /**
     * Scan the set of candidate interfaces that need to be signed
     *
     * @param targetClass
     */
    private void scanCandidateSignController(final Class<?> targetClass) {
        String rootPath = "";
        final Controller controller = AnnotationUtils.getAnnotation(targetClass, Controller.class);
        if (null != controller) {
            rootPath = controller.value();
        }

        if (StringUtils.isBlank(rootPath)) {
            final RequestMapping controllerMapping =
                    AnnotatedElementUtils.findMergedAnnotation(targetClass, RequestMapping.class);
            if (null != controllerMapping) {
                rootPath = controllerMapping.value()[0];
            }
        }

        final Method[] methods = targetClass.getDeclaredMethods();
        for (final Method method : methods) {
            if (AnnotatedElementUtils.hasAnnotation(method, RequestMapping.class)
                    && AnnotatedElementUtils.hasAnnotation(method, SignatureVerify.class)) {
                final RequestMapping requestMapping =
                        AnnotatedElementUtils.findMergedAnnotation(method, RequestMapping.class);
                final SignatureVerify signatureCheck =
                        AnnotationUtils.getAnnotation(method, SignatureVerify.class);
                if (requestMapping != null && requestMapping.path() != null) {
                    for (final String path : requestMapping.value()) {
                        if (StringUtils.isNotBlank(path)) {
                            candidateSignPathMap.put(
                                    getFullRequestPath(rootPath.trim(), path.trim()),
                                    signatureCheck);
                        }
                    }
                }
            }
        }
    }

    private String getFullRequestPath(String rootPath, String path) {
        rootPath = rootPath.endsWith("/") ? rootPath : rootPath + "/";
        path = path.startsWith("/") ? path.substring(1) : path;
        return rootPath + path;
    }

    /**
     * Get the target object based on the proxy object
     *
     * @param bean
     * @return
     */
    private Class<?> getTargetClass(final Object bean) {
        Object targetBean = bean;
        while (targetBean instanceof Advised && AopUtils.isAopProxy(targetBean)) {
            try {
                // target object
                targetBean = ((Advised) targetBean).getTargetSource().getTarget();
            } catch (Exception e) {
                log.error("SignatureScanner.getTargetClass error", e);
            }
        }
        return targetBean.getClass();
    }

    @Override
    public void stop() {
        isRunning = false;
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public int getPhase() {
        return phase;
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public void stop(final Runnable callback) {
        isRunning = false;
    }

}
