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
import com.alioth4j.corneast.core.algo.RandomSelector;
import com.alioth4j.corneast.core.algo.Selector;
import com.alioth4j.corneast.core.strategy.RequestHandlingStrategy;
import jakarta.annotation.PostConstruct;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Component(CorneastOperation.RELEASE)
public class ReleaseRequestHandlingStrategy implements RequestHandlingStrategy {

    @Autowired
    @Qualifier("releaseExecutor")
    private Executor releaseExecutor;

    @Autowired
    @Qualifier("redissonClients")
    private List<RedissonClient> redissonClients;

    private int nodeSize;

    @PostConstruct
    public void init() {
        this.nodeSize = redissonClients.size();
    }

    private static final Random random = new Random();

    private static final String luaScript = """
                                            local oldValue = redis.call('GET', KEYS[1])
                                            if not oldValue then
                                                oldValue = 0
                                            else
                                                oldValue = tonumber(oldValue)
                                            end
                                            redis.call('SET', KEYS[1], oldValue + 1)
                                            """;

    private static final ResponseProto.ResponseDTO.Builder responseBuilder = ResponseProto.ResponseDTO.newBuilder().setType(CorneastOperation.RELEASE);
    private static final ResponseProto.ReleaseRespDTO.Builder successRespBuilder = ResponseProto.ReleaseRespDTO.newBuilder().setSuccess(true);
//    private static final ResponseProto.ReleaseRespDTO.Builder failRespBuilder = ResponseProto.ReleaseRespDTO.newBuilder().setSuccess(false);

    private final Selector<RedissonClient> selector = new RandomSelector<>();

    private final Object builderLock = new Object();

    @Override
    public CompletableFuture<ResponseProto.ResponseDTO> handle(RequestProto.RequestDTO requestDTO) {
        return CompletableFuture.supplyAsync(() -> {
            String key = requestDTO.getReleaseReqDTO().getKey();
            selector.select(redissonClients).getScript().eval(RScript.Mode.READ_WRITE, luaScript, RScript.ReturnType.VALUE, List.of(key));
            synchronized (builderLock) {
                return responseBuilder
                        .setId(requestDTO.getId())
                        .setReleaseRespDTO(successRespBuilder.setKey(key).build())
                        .build();
            }
        }, releaseExecutor);
    }

    @Override
    public String getType() {
        return CorneastOperation.RELEASE;

    }

}
