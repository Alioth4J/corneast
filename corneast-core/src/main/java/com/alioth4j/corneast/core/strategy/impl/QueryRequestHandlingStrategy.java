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
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * Query request handling strategy.
 *
 * @author Alioth Null
 */
@Component(CorneastOperation.QUERY)
public class QueryRequestHandlingStrategy implements RequestHandlingStrategy {

    @Autowired
    @Qualifier("unifiedExecutor")
    private Executor unifiedExecutor;

    @Autowired
    @Qualifier("redissonClients")
    private List<RedissonClient> redissonClients;

    private static final ResponseProto.ResponseDTO.Builder responseBuilder = ResponseProto.ResponseDTO.newBuilder().setType(CorneastOperation.QUERY);
    private static final ResponseProto.QueryRespDTO.Builder queryResponseBuilder = ResponseProto.QueryRespDTO.newBuilder();

    private final Object builderLock = new Object();

    @Override
    public CompletableFuture<ResponseProto.ResponseDTO> handle(RequestProto.RequestDTO requestDTO) {
        return CompletableFuture.supplyAsync(() -> {
            // FIXME currently it is just an estimation, no atomicity warranty
            // sum tokenCount from each node
            String key = requestDTO.getQueryReqDTO().getKey();
            long totalTokenCount = 0;
            try {
                for (RedissonClient redissonClient : redissonClients) {
                    String value = redissonClient.<String>getBucket(key, StringCodec.INSTANCE).get();
                    totalTokenCount += value == null ? 0L : Long.parseLong(value);
                }
            } catch (NumberFormatException e) {
                throw new CorneastHandleException("Error executing redis commands during [query]", e);
            }
            synchronized (builderLock) {
                return responseBuilder
                        .setId(requestDTO.getId())
                        .setQueryRespDTO(queryResponseBuilder
                                .setKey(key)
                                .setRemainingTokenCount(totalTokenCount)
                                .build())
                        .build();
            }
        }, unifiedExecutor);
    }

    @Override
    public String getType() {
        return CorneastOperation.QUERY;
    }

}
