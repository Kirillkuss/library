package com.itrail.library.aspect.metrics;

import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import com.itrail.library.service.prometheus.MetricsService;
import java.util.HashMap;
import java.util.Map;
/**
 * 
 * MetricsAspect - Аспект для сборки метрик
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class MetricsAspect {

    private final MetricsService metricsService;

    /**
     * Pointcut для всех методов в контроллерах
     */
    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *) && " +
              "!within(com.itrail.library.controller.auth.AuthenticationController) && " + 
              "!within(com.itrail.library.controller.auth.LibraryErrorController)")
    public void controllerMethods() {}

    /**
     * Pointcut для всех методов в сервисах
     */
    @Pointcut("within(@org.springframework.stereotype.Service *)")
    public void serviceMethods() {}

    /**
     * Pointcut для методов с аннотацией @TrackMetrics
     */
    @Pointcut("@annotation(com.itrail.library.aspect.metrics.TrackMetrics)")
    public void trackedMethods() {}

    /**
     * Сбор метрик для контроллеров @RestController
     * @param joinPoint - Точка соед
     * @return Object
     * @throws Throwable
     */
    @Around("controllerMethods()")
    public Object trackControllerMetrics(ProceedingJoinPoint joinPoint) throws Throwable {
        return trackMethod(joinPoint, "controller");
    }

    /**
     * Сбор метрик для сервисов @Service
     * @param joinPoint - Точка соед
     * @return Object
     * @throws Throwable
     */
    @Around("serviceMethods()")
    public Object trackServiceMetrics(ProceedingJoinPoint joinPoint) throws Throwable {
        return trackMethod(joinPoint, "service");
    }

    /**
     * Сбор метрик для методов с аннотацией @TrackMetrics
     */
    @Around("trackedMethods()")
    public Object trackAnnotatedMetrics(ProceedingJoinPoint joinPoint) throws Throwable {
        return trackMethod(joinPoint, "annotated");
    }

    /**
     * Метод для метрики методов
     * @param joinPoint - Точка
     * @param layer - Слоу ( контроллер или сервис )
     * @return Object
     * @throws Throwable
     */
    private Object trackMethod(ProceedingJoinPoint joinPoint, String layer) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getMethod().getName();
        String className = signature.getDeclaringType().getSimpleName();
        String fullMethodName = className + "." + methodName;
        //log.info("Track method: {}, layer: {}", fullMethodName, layer);
        TrackMetrics annotation = signature.getMethod().getAnnotation(TrackMetrics.class);
        String metricName = annotation != null && !annotation.value().isEmpty() 
                ? annotation.value() 
                : fullMethodName;

        Map<String, String> tags = new HashMap<>();
        tags.put("layer", layer);
        tags.put("class", className);

        if (annotation != null && !annotation.tags().isEmpty()) {
            parseTags(annotation.tags(), tags);
        }

        Timer.Sample sample = metricsService.startTimer(metricName, tags);

        try {
            Object result = joinPoint.proceed();
            metricsService.incrementCounter(metricName, "success", tags);
            metricsService.stopTimer(sample, metricName, tags);
            return result;
        } catch (Exception e) {
            metricsService.incrementCounter(metricName, "error", tags);
            metricsService.incrementError(metricName, e.getClass().getSimpleName());
            metricsService.stopTimer(sample, metricName, tags);
            throw e;
        }
    }

    private void parseTags(String tagsStr, Map<String, String> targetMap) {
        try {
            String[] pairs = tagsStr.split(",");
            for (String pair : pairs) {
                String[] keyValue = pair.trim().split("=");
                if (keyValue.length == 2) {
                    targetMap.put(keyValue[0].trim(), keyValue[1].trim());
                }
            }
        } catch (Exception e) {
            log.warn("Failed to parse tags: {}", tagsStr, e);
        }
    }
}