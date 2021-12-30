package cz.laryngektomie.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RestLoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(RestLoggingAspect.class);

    @Pointcut("execution(* cz.laryngektomie.controller.rest.*.*(..))")
    public void loggingPointCut() {
        // pointcut method
    }

    @Before("loggingPointCut()")
    public void before(JoinPoint joinPoint) {
        logger.info("Rest call on endpoint {} ", joinPoint);
    }

    @AfterReturning("loggingPointCut()")
    public void after(JoinPoint joinPoint) {
        logger.info("After method invoked {}", joinPoint.getSignature());
    }
}
