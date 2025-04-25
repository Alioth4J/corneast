package com.alioth4j.corneast_core.aop;

import com.alioth4j.corneast_core.proto.RequestProto;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Aspect class for request statistics.
 *
 * @author Alioth Null
 */
@Aspect
@Component
public class RequestStatisticsAspect {

    private static final Logger log = LoggerFactory.getLogger(RequestStatisticsAspect.class);

    @Around("execution(* com.alioth4j.corneast_core.strategy.impl.RegisterRequestHandlingStrategy.handle(..))")
    public Object statRegisterRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        RequestProto.RequestDTO requestDTO = (RequestProto.RequestDTO) args[0];

        long startTime = System.nanoTime();
        try {
            Object result = joinPoint.proceed();
            long endTime = System.nanoTime();
            log.info("Stated Request: type={}, key={}, requestCount={}; processedTime={}", requestDTO.getType(), requestDTO.getRegisterReqDTO().getKey(), requestDTO.getRegisterReqDTO().getTokenCount(), endTime - startTime);
            return result;
        } catch (Throwable t) {
            log.error("Error stat request: type={}, key={}, requestCount={}", requestDTO.getType(), requestDTO.getRegisterReqDTO().getKey(), requestDTO.getRegisterReqDTO().getTokenCount());
            throw t;
        }
    }

    @Around("execution(* com.alioth4j.corneast_core.strategy.impl.ReduceRequestHandlingStrategy.handle(..))")
    public Object statReduceRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        RequestProto.RequestDTO requestDTO = (RequestProto.RequestDTO) args[0];

        long startTime = System.nanoTime();
        try {
            Object result = joinPoint.proceed();
            long endTime = System.nanoTime();
            log.info("Stated Request: type={}, key={}; processedTime={}", requestDTO.getType(), requestDTO.getReduceReqDTO().getKey(), endTime - startTime);
            return result;
        } catch (Throwable t) {
            log.error("Error stat request: type={}, key={}", requestDTO.getType(), requestDTO.getRegisterReqDTO().getKey());
            throw t;
        }
    }

    @Around("execution(* com.alioth4j.corneast_core.strategy.impl.QueryRequestHandlingStrategy.handle(..))")
    public Object statQueryRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        RequestProto.RequestDTO requestDTO = (RequestProto.RequestDTO) args[0];

        long startTime = System.nanoTime();
        try {
            Object result = joinPoint.proceed();
            long endTime = System.nanoTime();
            log.info("Stated Request: type={}, key={}; processedTime={}", requestDTO.getType(), requestDTO.getQueryReqDTO().getKey(), endTime - startTime);
            return result;
        } catch (Throwable t) {
            log.error("Error stat request: type={}, key={}", requestDTO.getType(), requestDTO.getRegisterReqDTO().getKey());
            throw t;
        }
    }

}
