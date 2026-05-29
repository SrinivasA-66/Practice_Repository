package com.cts.openbankx;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggerAspect {

    private static final Logger log =
            LoggerFactory.getLogger(LoggerAspect.class);

    @Pointcut("execution(* com.cts.openbankx..service..*(..))")
    public void serviceMethods() {}

    @Before("serviceMethods()")
    public void before(JoinPoint jp) {
        log.info("Entering: {}.{}",
                jp.getTarget().getClass().getSimpleName(),
                jp.getSignature().getName());
    }

    @AfterReturning("serviceMethods()")
    public void afterSuccess(JoinPoint jp) {
        log.info("Success: {}.{}",
                jp.getTarget().getClass().getSimpleName(),
                jp.getSignature().getName());
    }

    @AfterThrowing(value = "serviceMethods()", throwing = "ex")
    public void afterFailure(JoinPoint jp, Throwable ex) {
        log.error("Exception in {}.{} – {}",
                jp.getTarget().getClass().getSimpleName(),
                jp.getSignature().getName(),
                ex.getMessage());
    }
}