package com.itrail.library.config.redis;

import java.lang.reflect.Method;
import java.util.UUID;
import org.springframework.cache.interceptor.KeyGenerator;

public class LibKeyGenerator implements KeyGenerator {

    public Object generate(Object target, Method method, Object... params) {
        return UUID.randomUUID().toString();
        /**+ " for " + target.getClass().getSimpleName() + " - " + method.getName() + " - "
                + StringUtils.arrayToDelimitedString(params, "_");*/
    }
}