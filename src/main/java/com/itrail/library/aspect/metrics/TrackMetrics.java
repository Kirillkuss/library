package com.itrail.library.aspect.metrics;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * TrackMetrics - Аннотация для автоматического сбора метрик
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TrackMetrics {

    /**
     * Название метрики (если не указано, используется имя класса + метода)
     * @return String
     */
    public String value() default "";

    /**
     * Тип слоя: controller или service
     * @return String
     */
    public String layer() default "default";

    /**
     * Дополнительные теги в формате "key1=value1,key2=value2"
     * @return String
     */
    public String tags() default "";

    /**
     * Собирать ли метрики для успешных вызовов
     * @return boolean
     */
    public boolean trackSuccess() default true;

    /**
     * Собирать ли метрики для ошибок
     * @return boolean
     */
    public boolean trackErrors() default true;
}