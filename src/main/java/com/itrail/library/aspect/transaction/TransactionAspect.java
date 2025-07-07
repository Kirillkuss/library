package com.itrail.library.aspect.transaction;

import java.lang.reflect.Method;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
public class TransactionAspect {

    @Pointcut("@annotation(org.springframework.transaction.annotation.Transactional)")
    public void transactionalMethod() {}

    @Pointcut("within(@org.springframework.transaction.annotation.Transactional *)")
    public void transactionalClass() {}

    @Around("transactionalMethod() || transactionalClass()")
    public Object monitorTransaction(ProceedingJoinPoint joinPoint) throws Throwable {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        Transactional transactional = method.getAnnotation(Transactional.class);
        String methodName = joinPoint.getSignature().toShortString();
        try {
            if (transactional != null) {
                logTransactionStart(methodName, transactional);
            } else {
                log.info("Transaction START [{}] - default parameters", methodName);
            }
            long startTime = System.currentTimeMillis();
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;
            log.info("Transaction SUCCESS [{}] - execution time: {} ms", methodName, executionTime);
            return result;
        } catch (Throwable ex) {
            if (transactional != null) {
                boolean shouldRollback = shouldRollback(transactional, ex);
                
                if (shouldRollback) {
                    log.error("Transaction ROLLBACK [{}] due to: {} - {}", 
                        methodName, ex.getClass().getSimpleName(), ex.getMessage());
                } else {
                    log.warn("Transaction COMMITTED [{}] despite exception: {} - {}", 
                        methodName, ex.getClass().getSimpleName(), ex.getMessage());
                }
            } else {
                log.error("Transaction ROLLBACK [{}] (default) due to: {} - {}", 
                    methodName, ex.getClass().getSimpleName(), ex.getMessage());
            }
            throw ex;
        }
    }

    private void logTransactionStart(String methodName, Transactional transactional) {
        String transactionInfo = String.format("propagation=%s, isolation=%s, timeout=%d, readOnly=%s",
            transactional.propagation().name(),
            transactional.isolation().name(),
            transactional.timeout(),
            transactional.readOnly());

        log.info("Transaction START [{}] - {}", methodName, transactionInfo);
    }

    private boolean shouldRollback(Transactional transactional, Throwable ex) {
        for (Class<?> rollbackFor : transactional.rollbackFor()) {
            if (rollbackFor.isInstance(ex)) {
                return true;
            }
        }
        for (Class<?> noRollbackFor : transactional.noRollbackFor()) {
            if (noRollbackFor.isInstance(ex)) {
                return false;
            }
        }
        return (ex instanceof RuntimeException || ex instanceof Error);
    }

}
