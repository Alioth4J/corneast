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

package com.alioth4j.corneast_core.ringbuffer;

import com.alioth4j.corneast_core.common.CorneastOperation;
import com.alioth4j.corneast_core.proto.ResponseProto;
import com.lmax.disruptor.WorkHandler;
import jakarta.annotation.PostConstruct;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

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

    private static final Map<String, ResponseProto.ResponseDTO.Builder> cachedSuccessResponses = new ConcurrentHashMap<>();
    private static final Map<String, ResponseProto.ResponseDTO.Builder> cachedFailResponses = new ConcurrentHashMap<>();

    private static final Random random = new Random();

    // reused objects
    private static final ResponseProto.ResponseDTO.Builder responseBuilder = ResponseProto.ResponseDTO.newBuilder().setType(CorneastOperation.REDUCE);
    private static final ResponseProto.ReduceRespDTO.Builder successReduceRespBuilder = ResponseProto.ReduceRespDTO.newBuilder().setSuccess(true);
    private static final ResponseProto.ReduceRespDTO.Builder failReduceRespBuilder = ResponseProto.ReduceRespDTO.newBuilder().setSuccess(false);

    private static final String luaScript = """
                                            local n = tonumber(redis.call('GET', KEYS[1]) or "0")
                                            if n > 0 then
                                                redis.call('DECR', KEYS[1])
                                                return 1
                                            else
                                                return 0
                                            end
                                            """;

    @Override
    public void onEvent(ReduceEvent reduceEvent) throws Exception {
        String key = reduceEvent.getKey();
        CompletableFuture<ResponseProto.ResponseDTO> future = reduceEvent.getFuture();

        ResponseProto.ResponseDTO responseDTO;
        // pick a redissonClient randomly
        RedissonClient redissonClient = redissonClients.get(random.nextInt(nodeSize));
        long result = redissonClient.getScript().eval(RScript.Mode.READ_WRITE, luaScript, RScript.ReturnType.INTEGER, List.of(key));
        if (result == 1) {
            if (!cachedSuccessResponses.containsKey(key)) {
                cachedSuccessResponses.put(key, responseBuilder
                        .setReduceRespDTO(successReduceRespBuilder
                                .setKey(key)
                                .build())
                        );
            }
            responseDTO = cachedSuccessResponses.get(key)
                          .setId(reduceEvent.getId()).build();
        } else {
            if (!cachedFailResponses.containsKey(key)) {
                cachedFailResponses.put(key, responseBuilder
                        .setReduceRespDTO(failReduceRespBuilder
                                .setKey(key)
                                .build())
                        );
            }
            responseDTO = cachedFailResponses.get(key)
                          .setId(reduceEvent.getId()).build();
        }
        future.complete(responseDTO);
    }

}
