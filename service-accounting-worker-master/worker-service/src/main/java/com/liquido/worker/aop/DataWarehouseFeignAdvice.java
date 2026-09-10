package com.liquido.worker.aop;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.worker.common.properties.WorkerProperties;
import com.liquido.worker.pojo.bo.DwPage;
import com.liquido.worker.pojo.bo.DwResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class DataWarehouseFeignAdvice {

    private final WorkerProperties.DataWarehouseProperties dataWarehouseProperties;

    @Around("execution(* com.liquido.worker.feign.DataWarehouseFeign.*(..))")
    public Object aroundFeignMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        final Map<String, WorkerProperties.DataWarehouseEnvProperties> allEnv =
                dataWarehouseProperties.getEnv().entrySet().stream()
                        .filter(x -> Optional.ofNullable(x.getValue().getEnable()).orElse(false))
                        .collect(Collectors.toMap(x -> x.getKey(), x -> x.getValue()));
        final Set<String> allEnvKey = allEnv.keySet();
        final Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        final Parameter[] parameters = method.getParameters();
        final Map<String, ParamInfo> paramInfoMap =
                IntStream.range(0, method.getParameters().length).mapToObj(i ->
                                new ParamInfo(parameters[i].getName(), i, joinPoint.getArgs()[i]))
                        .collect(Collectors.toMap(ParamInfo::getName, Function.identity()));
        final ParamInfo env = paramInfoMap.get("env");
        final ParamInfo tryAllEnvFlag = paramInfoMap.get("tryAllEnvFlag");
        final List<String> tryEnv = Lists.newArrayList();
        DwResponse<Object> result = DwResponse.builder().code(500).build();
        while (true) {
            if (tryEnv.isEmpty() && Objects.nonNull(env.getValue())) {
                result = (DwResponse<Object>) joinPoint.proceed();
                tryEnv.add(env.getValue().toString());
            } else {
                final String otherEnv = allEnvKey.stream().filter(x -> !tryEnv.contains(x))
                        .findFirst().orElse(null);
                if (Objects.isNull(otherEnv)) {
                    return result;
                }
                Object[] args = joinPoint.getArgs();
                args[env.getIndex()] = TransactionTypeCodeEnum.parse(otherEnv);
                result = (DwResponse<Object>) joinPoint.proceed(args);
                tryEnv.add(otherEnv);
            }
            if (!(Boolean) Optional.ofNullable(tryAllEnvFlag.getValue()).orElse(false)) {
                return result;
            }
            if (result.isSuccess() && Objects.nonNull(result.getData())
                    && !(result.getData() instanceof DwPage)) {
                return result;
            }
            if (result.isSuccess() && Objects.nonNull(result.getData())
                    && result.getData() instanceof DwPage
                    && ((DwPage<Object>) result.getData()).getTotalCount() > 0) {
                return result;
            }
            if (tryEnv.containsAll(allEnvKey)) {
                return result;
            }
        }
    }

    @Data
    @AllArgsConstructor
    private static class ParamInfo {

        private String name;

        private int index;

        private Object value;

    }

}
