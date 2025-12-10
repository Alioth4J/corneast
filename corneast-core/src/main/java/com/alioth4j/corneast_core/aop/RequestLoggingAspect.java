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

import com.alioth4j.corneast_common.proto.RequestProto;
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

    @Around("execution(* com.alioth4j.corneast_core.strategy.impl.RegisterRequestHandlingStrategy.handle(..)) || " +
            "execution(* com.alioth4j.corneast_core.strategy.impl.ReduceRequestHandlingStrategy.handle(..)) || " +
            "execution(* com.alioth4j.corneast_core.strategy.impl.QueryRequestHandlingStrategy.handle(..)) || " +
            "execution(* com.alioth4j.corneast_core.strategy.impl.ReleaseRequestHandlingStrategy.handle(..))")
    public Object logRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        RequestProto.RequestDTO requestDTO = (RequestProto.RequestDTO) args[0];
        if (log.isInfoEnabled()) {
            log.info("Received request: id = {}", requestDTO.getId());
        }
        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable t) {
            log.error("Error processing request: id = {}", requestDTO.getId(), t);
            throw t;
        }
        return result;
    }

}
