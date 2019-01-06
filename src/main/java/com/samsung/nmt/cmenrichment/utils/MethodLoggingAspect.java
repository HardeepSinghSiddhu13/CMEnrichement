/*package com.samsung.nmt.cmenrichment.utils;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aspect
public class MethodLoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(MethodLoggingAspect.class);

    @Pointcut("execution(** com.samsung.nmt.cmenrichment.processor.BatchProcessor.process(..)) || "
            + " execution(** com.samsung.nmt.cmenrichment.storage.BatchStorageManager.cudProcessedData(..))")
    public void log() {
    }

    @Around("log()")
    public void watchPerformance(ProceedingJoinPoint jp) throws Throwable {

        StringBuilder enter = new StringBuilder();
        enter.append(jp.getClass())
                .append("-")
                .append(jp.getSignature().toShortString())
                .append("Args : ")
                .append(jp.getArgs());

        logger.info(enter.toString());
        Object object = jp.proceed();

        StringBuilder exit = new StringBuilder();
        exit.append(jp.getClass())
                .append("-")
                .append(jp.getSignature().toShortString())
                .append("Return : ")
                .append(object);
    }
}
*/