/*
 * Corneast
 * Copyright (C) 2025 Alioth Null
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
        Object result;
        try {
            result = joinPoint.proceed();
            long endTime = System.nanoTime();
            log.info("Stated Request: type={}, key={}, requestCount={}; processedTime={}", requestDTO.getType(), requestDTO.getRegisterReqDTO().getKey(), requestDTO.getRegisterReqDTO().getTokenCount(), endTime - startTime);
        } catch (Throwable t) {
            log.error("Error stat request: type={}, key={}, requestCount={}", requestDTO.getType(), requestDTO.getRegisterReqDTO().getKey(), requestDTO.getRegisterReqDTO().getTokenCount());
            throw t;
        }
        return result;
    }

    @Around("execution(* com.alioth4j.corneast_core.strategy.impl.ReduceRequestHandlingStrategy.handle(..))")
    public Object statReduceRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        RequestProto.RequestDTO requestDTO = (RequestProto.RequestDTO) args[0];

        long startTime = System.nanoTime();
        Object result;
        try {
            result = joinPoint.proceed();
            long endTime = System.nanoTime();
            log.info("Stated Request: type={}, key={}; processedTime={}", requestDTO.getType(), requestDTO.getReduceReqDTO().getKey(), endTime - startTime);
        } catch (Throwable t) {
            log.error("Error stat request: type={}, key={}", requestDTO.getType(), requestDTO.getReduceReqDTO().getKey());
            throw t;
        }
        return result;
    }

    @Around("execution(* com.alioth4j.corneast_core.strategy.impl.QueryRequestHandlingStrategy.handle(..))")
    public Object statQueryRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        RequestProto.RequestDTO requestDTO = (RequestProto.RequestDTO) args[0];

        long startTime = System.nanoTime();
        Object result;
        try {
            result = joinPoint.proceed();
            long endTime = System.nanoTime();
            log.info("Stated Request: type={}, key={}; processedTime={}", requestDTO.getType(), requestDTO.getQueryReqDTO().getKey(), endTime - startTime);
        } catch (Throwable t) {
            log.error("Error stat request: type={}, key={}", requestDTO.getType(), requestDTO.getQueryReqDTO().getKey());
            throw t;
        }
        return result;
    }

    @Around("execution(* com.alioth4j.corneast_core.strategy.impl.ReleaseRequestHandlingStrategy.handle(..))")
    public Object statReleaseRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        RequestProto.RequestDTO requestDTO = (RequestProto.RequestDTO) args[0];

        long startTime = System.nanoTime();
        Object result;
        try {
            result = joinPoint.proceed();
            long endTime = System.nanoTime();
            log.info("Stated Request: type={}, key={}; processedTime={}", requestDTO.getType(), requestDTO.getReleaseReqDTO().getKey(), endTime - startTime);
        } catch (Throwable t) {
            log.error("Error stat request: type={}, key={}", requestDTO.getType(), requestDTO.getReleaseReqDTO().getKey());
            throw t;
        }
        return result;
    }

}
