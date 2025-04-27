package com.alioth4j.corneast_core.aop;

import com.alioth4j.corneast_core.proto.RequestProto;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Aspect class for request handling logging.
 *
 * @author Alioth Null
 */
@Aspect
@Component
public class RequestLoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingAspect.class);

    @Around("execution(* com.alioth4j.corneast_core.strategy.impl.RegisterRequestHandlingStrategy.handle(..))")
    public Object logRegisterRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        RequestProto.RequestDTO requestDTO = (RequestProto.RequestDTO) args[0];
        log.info("Received request: type={}, key={}, tokenCount={}", requestDTO.getType(), requestDTO.getRegisterReqDTO().getKey(), requestDTO.getRegisterReqDTO().getTokenCount());
        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable t) {
            log.error("Error processing request: type={}, key={}, tokenCount={}", requestDTO.getType(), requestDTO.getRegisterReqDTO().getKey(), requestDTO.getRegisterReqDTO().getTokenCount());
            throw t;
        }
        return result;
    }

    @Around("execution(* com.alioth4j.corneast_core.strategy.impl.ReduceRequestHandlingStrategy.handle(..))")
    public Object logReduceRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        RequestProto.RequestDTO requestDTO = (RequestProto.RequestDTO) args[0];
        log.info("Received request: type={}, key={}", requestDTO.getType(), requestDTO.getReduceReqDTO().getKey());
        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable t) {
            log.error("Error processing request: type={}, key={}", requestDTO.getType(), requestDTO.getReduceReqDTO().getKey());
            throw t;
        }
        return result;
    }

    @Around("execution(* com.alioth4j.corneast_core.strategy.impl.QueryRequestHandlingStrategy.handle(..))")
    public Object logQueryRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        RequestProto.RequestDTO requestDTO = (RequestProto.RequestDTO) args[0];
        log.info("Received request: type={}, key={}", requestDTO.getType(), requestDTO.getQueryReqDTO().getKey());
        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable t) {
            log.error("Error processing request: type={}, key={}", requestDTO.getType(), requestDTO.getRegisterReqDTO().getKey());
            throw t;
        }
        return result;
    }

}
