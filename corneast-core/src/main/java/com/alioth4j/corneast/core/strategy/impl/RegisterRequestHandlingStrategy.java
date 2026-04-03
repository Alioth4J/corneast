/*
 * Corneast
 * Copyright (C) 2025-2026 Alioth Null
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

package com.alioth4j.corneast.core.strategy.impl;

import com.alioth4j.corneast.common.operation.CorneastOperation;
import com.alioth4j.corneast.common.proto.RequestProto;
import com.alioth4j.corneast.common.proto.ResponseProto;
import com.alioth4j.corneast.core.exception.CorneastHandleException;
import com.alioth4j.corneast.core.strategy.RequestHandlingStrategy;
import jakarta.annotation.PostConstruct;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 * Register request handling strategy.
 *
 * @author Alioth Null
 */
@Component(CorneastOperation.REGISTER)
public class RegisterRequestHandlingStrategy implements RequestHandlingStrategy {

    @Autowired
    @Qualifier("unifiedExecutor")
    private Executor unifiedExecutor;

    @Autowired
    @Qualifier("redissonClients")
    private List<RedissonClient> redissonClients;

    private int nodeSize;

    @PostConstruct
    public void init() {
        this.nodeSize = redissonClients.size();
    }

    private static final ResponseProto.ResponseDTO.Builder responseBuilder = ResponseProto.ResponseDTO.newBuilder().setType(CorneastOperation.REGISTER);
    private static final ResponseProto.RegisterRespDTO.Builder successRespBuilder = ResponseProto.RegisterRespDTO.newBuilder().setSuccess(true);
    private static final ResponseProto.RegisterRespDTO.Builder failRespBuilder = ResponseProto.RegisterRespDTO.newBuilder().setSuccess(false);

    private final Object builderLock = new Object();

    @Override
    public CompletableFuture<ResponseProto.ResponseDTO> handle(RequestProto.RequestDTO requestDTO) {
        return CompletableFuture.supplyAsync(() -> {
            // distribute tokenCount to all the nodes evenly
            String key = requestDTO.getRegisterReqDTO().getKey();
            long totalTokenCount = requestDTO.getRegisterReqDTO().getTokenCount();
            long averageTokenCount = totalTokenCount / nodeSize;
            long remainingTokenCount = totalTokenCount % nodeSize;
            try {
                for (int i = 0; i < nodeSize; i++) {
                    RedissonClient redissonClient = redissonClients.get(i);
                    if (remainingTokenCount-- > 0) {
                        redissonClient.getBucket(key, StringCodec.INSTANCE).set(averageTokenCount + 1, 3600, TimeUnit.SECONDS);
                    } else {
                        redissonClient.getBucket(key, StringCodec.INSTANCE).set(averageTokenCount, 3600, TimeUnit.SECONDS);
                    }
                }
            } catch (Exception e) {
                throw new CorneastHandleException("Error executing redis commands during [register]", e);
            }
            synchronized (builderLock) {
                return responseBuilder
                        .setId(requestDTO.getId())
                        .setRegisterRespDTO(successRespBuilder
                                .setKey(key)
                                .build())
                        .build();
            }
        }, unifiedExecutor);
    }

    @Override
    public String getType() {
        return CorneastOperation.REGISTER;
    }

}
