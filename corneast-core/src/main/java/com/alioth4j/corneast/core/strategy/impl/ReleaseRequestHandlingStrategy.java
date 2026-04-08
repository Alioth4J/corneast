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
import com.alioth4j.corneast.common.algo.RandomSelector;
import com.alioth4j.corneast.common.algo.Selector;
import com.alioth4j.corneast.core.exception.CorneastHandleException;
import com.alioth4j.corneast.core.strategy.RequestHandlingStrategy;
import jakarta.annotation.PostConstruct;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.ByteArrayCodec;
import org.redisson.client.codec.StringCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

@Component(CorneastOperation.RELEASE)
public class ReleaseRequestHandlingStrategy implements RequestHandlingStrategy {

    @Autowired
    @Qualifier("unifiedExecutor")
    private Executor unifiedExecutor;

    @Autowired
    @Qualifier("redissonClients")
    private List<RedissonClient> redissonClients;

    @Autowired
    @Qualifier("idempotentRedissonClient")
    private RedissonClient idempotentRedissonClient;

    private int nodeSize;

    private Selector<RedissonClient> selector;

    @PostConstruct
    public void init() {
        this.nodeSize = redissonClients.size();
        selector = new RandomSelector<>(redissonClients);
    }

    private static final ResponseProto.ResponseDTO.Builder responseBuilder = ResponseProto.ResponseDTO.newBuilder().setType(CorneastOperation.RELEASE);
    private static final ResponseProto.ReleaseRespDTO.Builder successRespBuilder = ResponseProto.ReleaseRespDTO.newBuilder().setSuccess(true);
//    private static final ResponseProto.ReleaseRespDTO.Builder failRespBuilder = ResponseProto.ReleaseRespDTO.newBuilder().setSuccess(false);

    private final Object builderLock = new Object();

    @Override
    public CompletableFuture<ResponseProto.ResponseDTO> handle(RequestProto.RequestDTO requestDTO) {
        return CompletableFuture.supplyAsync(() -> {
            String key = requestDTO.getReleaseReqDTO().getKey();
            String id = requestDTO.getId();
            try {
                selector.select().getAtomicLong(key).incrementAndGet();
            } catch (Exception e) {
                throw new CorneastHandleException("Error executing lua script during [release]", e);
            }
            // construct response
            ResponseProto.ResponseDTO responseDTO = null;
            synchronized (builderLock) {
                responseDTO = responseBuilder
                        .setId(id)
                        .setReleaseRespDTO(successRespBuilder.setKey(key).build())
                        .build();
            }
            // set to idempotent redis
            if (!id.isEmpty()) {
                idempotentRedissonClient.<byte[]>getBucket(id, ByteArrayCodec.INSTANCE).set(responseDTO.toByteArray(), 10, TimeUnit.SECONDS);
            }
            return responseDTO;
        }, unifiedExecutor);
    }

    @Override
    public String getType() {
        return CorneastOperation.RELEASE;

    }

}
