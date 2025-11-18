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

package com.alioth4j.corneast_core.strategy.impl;

import com.alioth4j.corneast_core.common.CorneastOperation;
import com.alioth4j.corneast_core.proto.RequestProto;
import com.alioth4j.corneast_core.proto.ResponseProto;
import com.alioth4j.corneast_core.strategy.RequestHandlingStrategy;
import jakarta.annotation.PostConstruct;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * Register request handling strategy.
 *
 * @author Alioth Null
 */
@Component(CorneastOperation.REGISTER)
public class RegisterRequestHandlingStrategy implements RequestHandlingStrategy {

    @Autowired
    @Qualifier("registerExecutor")
    private Executor registerExecutor;

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

    private static final String luaScript = """
                                            redis.call('SET', KEYS[1], ARGV[1])
                                            """;

    @Override
    public CompletableFuture<ResponseProto.ResponseDTO> handle(RequestProto.RequestDTO requestDTO) {
        return CompletableFuture.supplyAsync(() -> {
            // distribute tokenCount to all the nodes evenly
            String key = requestDTO.getRegisterReqDTO().getKey();
            long totalTokenCount = requestDTO.getRegisterReqDTO().getTokenCount();
            long averageTokenCount = totalTokenCount / nodeSize;
            long remainingTokenCount = totalTokenCount % nodeSize;
            for (int i = 0; i < nodeSize; i++) {
                RedissonClient redissonClient = redissonClients.get(i);
                long curTokenCount = averageTokenCount;
                if (remainingTokenCount > 0) {
                    curTokenCount++;
                    remainingTokenCount--;
                }
                redissonClient.getScript(StringCodec.INSTANCE).eval(RScript.Mode.READ_WRITE, luaScript, RScript.ReturnType.VALUE, List.of(key), curTokenCount);
            }
            return responseBuilder
                   .setId(requestDTO.getId())
                   .setRegisterRespDTO(successRespBuilder
                                       .setKey(key)
                                       .build())
                   .build();
        }, registerExecutor);
    }

}
