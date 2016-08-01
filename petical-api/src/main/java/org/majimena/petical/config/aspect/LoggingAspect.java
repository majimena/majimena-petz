package org.majimena.petical.config.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import javax.inject.Inject;
import java.util.Arrays;

/**
 * Aspect for logging execution of service and repository Spring components.
 */
@Aspect
public class LoggingAspect {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Inject
    private Environment env;

    @Pointcut("within(org.majimena.petical.repository..*) || within(org.majimena.petical.service..*)")
    public void loggingPointcut() {
    }

    @AfterThrowing(pointcut = "loggingPointcut()", throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
        log.error("Exception in {}.{}() with cause = {}", joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(), e.getCause());
    }

    @Around("loggingPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        if (log.isDebugEnabled()) {
            try {
                log.debug("Enter: {}.{}() with argument[s] = {}", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName(), Arrays.toString(joinPoint.getArgs()));
            } catch (Exception e) {
                // ignore exception
            }
        }
        try {
            Object result = joinPoint.proceed();
            if (log.isTraceEnabled()) {
                try {
                    log.trace("Exit: {}.{}() with result = {}", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName(), result);
                } catch (Exception e) {
                    // ignore exception
                }
            }
            return result;
        } catch (IllegalArgumentException e) {
            log.error("Illegal argument: {} in {}.{}()", Arrays.toString(joinPoint.getArgs()),
                    joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());

            throw e;
        }
    }
}
