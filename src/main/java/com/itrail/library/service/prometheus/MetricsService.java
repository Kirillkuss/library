package com.itrail.library.service.prometheus;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
@Component
@RequiredArgsConstructor
public class MetricsService {

    private final MeterRegistry meterRegistry;

    private final ConcurrentMap<String, Counter> counters      = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Timer>   timers        = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Counter> errorCounters = new ConcurrentHashMap<>();

    /**
     * Увеличить счетчик с дополнительными тегами
     * @param methodName     - Наименование метода
     * @param status         - Стаутас
     * @param additionalTags - Тэг
     */
    public void incrementCounter(String methodName, String status, Map<String, String> additionalTags) {
        String key = methodName + ":" + status + ":" + additionalTags.hashCode();
        Counter.Builder builder = Counter.builder( "library.api.requests" )
                                         .tag( "method", methodName )
                                         .tag( "status", status );
        if (additionalTags != null) {
            additionalTags.forEach(builder::tag);
        }
        Counter counter = counters.computeIfAbsent(key, k -> builder.description( "Количество запросов к API в web library" )
                                                                    .register( meterRegistry ));
        counter.increment();
    }

    /**
     * Увеличить счетчик без тега
     * @param methodName - Наименование метода
     * @param status     - Стаптус
     */
    public void incrementCounter(String methodName, String status) {
        incrementCounter( methodName, status, null );
    }
    /**
     * Таймер - старт
     * @param methodName     - Наименование метода
     * @param additionalTags - Тэг
     * @return Timer.Sample
     */
    public Timer.Sample startTimer(String methodName, Map<String, String> additionalTags) {
        Timer.Builder builder = Timer.builder("library.api.duration").tag("method", methodName).publishPercentiles(0.5, 0.95, 0.99);
        if (additionalTags != null) {
            additionalTags.forEach( builder::tag );
        }
        return Timer.start( meterRegistry );
    }

    /**
     * Таймер - старт
     * @param methodName - Наименование метода
     * @return Timer.Sample
     */
    public Timer.Sample startTimer(String methodName) {
        return startTimer( methodName, null );
    }
    /**
     * Тймер - стоп
     * @param sample     - Время
     * @param methodName - Наименование метода
     */
    public void stopTimer( Timer.Sample sample, String methodName) {
        stopTimer( sample, methodName, null );
    }
    /**
     * Таймер - стоп
     * @param sample         - Время
     * @param methodName     - Наименование метода
     * @param additionalTags - Тэг
     */
    public void stopTimer(Timer.Sample sample, String methodName, Map<String, String> additionalTags) {
        String key = methodName + ":" + additionalTags.hashCode();
        Timer.Builder builder = Timer.builder( "library.api.duration" )
                                     .tag( "method", methodName )
                                     .publishPercentiles(0.5, 0.95, 0.99);
        if (additionalTags != null) {
            additionalTags.forEach( builder::tag );
        }
        Timer timer = timers.computeIfAbsent( key, k ->  builder.description( "Время выполнения методов API в web library" )
                                                               .register( meterRegistry ));
        sample.stop(timer);
    }

    /**
     * Запись кэша
     * @param cacheName - Наименование кэша
     * @param hit
     */
    public void recordCacheHit(String cacheName, boolean hit) {
         Counter.builder( "library.cache.operations" )
                .tag( "cache", cacheName )
                .tag( "result", hit ? "hit" : "miss" )
                .description("Операции с кэшем в web library" )
                .register( meterRegistry )
                .increment();
    }
    /**
     * Счетчик на ошибки
     * @param methodName - Наименование метода
     * @param errorType  - Тип ошибки
     */
    public void incrementError(String methodName, String errorType) {
        String key = methodName + ":" + errorType;
        Counter counter = errorCounters.computeIfAbsent(key, k ->
        Counter.builder("library.api.errors")
               .tag( "method", methodName )
               .tag( "error_type", errorType )
               .description( "Ошибки в API в web library" )
               .register( meterRegistry ));
        counter.increment();
    }
}
