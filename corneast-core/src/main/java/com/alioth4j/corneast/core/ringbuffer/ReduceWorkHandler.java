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

package com.alioth4j.corneast.core.ringbuffer;

import com.alioth4j.corneast.common.operation.CorneastOperation;
import com.alioth4j.corneast.common.proto.ResponseProto;
import com.alioth4j.corneast.core.algo.RandomSelector;
import com.alioth4j.corneast.core.algo.Selector;
import com.lmax.disruptor.WorkHandler;
import jakarta.annotation.PostConstruct;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

/**
 * Processes reduce requests.
 *
 * @author Alioth Null
 */
@Component
@Scope("prototype")
public class ReduceWorkHandler implements WorkHandler<ReduceEvent>  {

    @Autowired
    @Qualifier("redissonClients")
    private List<RedissonClient> redissonClients;

    private int nodeSize;

    @PostConstruct
    public void init() {
        this.nodeSize = redissonClients.size();
    }

    // reused objects
    private static final ResponseProto.ResponseDTO.Builder responseBuilder = ResponseProto.ResponseDTO.newBuilder().setType(CorneastOperation.REDUCE);
    private static final ResponseProto.ReduceRespDTO.Builder successReduceRespBuilder = ResponseProto.ReduceRespDTO.newBuilder().setSuccess(true);
    private static final ResponseProto.ReduceRespDTO.Builder failReduceRespBuilder = ResponseProto.ReduceRespDTO.newBuilder().setSuccess(false);

    private final Object builderLock = new Object();

    private static final String luaScript = """
                                            local n = tonumber(redis.call('GET', KEYS[1]) or "0")
                                            if n > 0 then
                                                redis.call('DECR', KEYS[1])
                                                return 1
                                            else
                                                return 0
                                            end
                                            """;

    private final Selector<RedissonClient> selector = new RandomSelector<>();

    @Override
    public void onEvent(ReduceEvent reduceEvent) throws Exception {
        String key = reduceEvent.getKey();
        CompletableFuture<ResponseProto.ResponseDTO> future = reduceEvent.getFuture();

        ResponseProto.ResponseDTO responseDTO;
        // pick a redissonClient randomly
        RedissonClient redissonClient = selector.select(redissonClients);
        long result = redissonClient.getScript().eval(RScript.Mode.READ_WRITE, luaScript, RScript.ReturnType.INTEGER, List.of(key));
        if (result == 1) {
            synchronized (builderLock) {
                responseDTO = responseBuilder.setId(reduceEvent.getId())
                                             .setReduceRespDTO(successReduceRespBuilder
                                                               .setKey(key)
                                                               .build())
                                             .build();
            }
        } else {
            synchronized (builderLock) {
                responseDTO = responseBuilder.setId(reduceEvent.getId())
                                             .setReduceRespDTO(failReduceRespBuilder
                                                              .setKey(key)
                                                              .build())
                                             .build();
            }
        }
        future.complete(responseDTO);
    }

}
