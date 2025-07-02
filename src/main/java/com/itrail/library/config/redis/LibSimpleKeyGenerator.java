package com.itrail.library.config.redis;

import java.lang.reflect.Method;
import java.util.UUID;
import org.springframework.cache.interceptor.SimpleKey;
import org.springframework.cache.interceptor.SimpleKeyGenerator;

public class LibSimpleKeyGenerator  extends SimpleKeyGenerator{

    @Override
    public Object generate(Object target, Method method, Object... params) {
        if (params.length == 0) {
            return SimpleKey.EMPTY;
        }else{
            return new SimpleKey( UUID.randomUUID().toString());
        }
    }
    
}
