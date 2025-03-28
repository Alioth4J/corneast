package com.alioth4j.corneast_core.service.impl;

import com.alioth4j.corneast_core.proto.ReduceProto;
import com.alioth4j.corneast_core.service.ReduceService;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
public class ReduceServiceImpl implements ReduceService {

    private static final String luaScript = """
                                            local n = tonumber(redis.call('GET', KEYS[1]) or "0")
                                            if n > 0 then
                                                redis.call('DECR', KEYS[1])
                                                return 1
                                            else 
                                                return 0
                                            end
                                            """;

    @Autowired
    private List<RedissonClient> redissonClients;

    @Async("reduceExecutor")
    @Override
    public CompletableFuture<ReduceProto.ReduceRespDTO> reduce(ReduceProto.ReduceReqDTO reduceReqDTO) {
        return CompletableFuture.supplyAsync(() -> {
            // pick a redissonClient randomly
            Random random = new Random();
            int nodeSize = redissonClients.size();
            RedissonClient redissonClient = redissonClients.get(random.nextInt(nodeSize));
            long result = redissonClient.getScript().eval(RScript.Mode.READ_WRITE, luaScript, RScript.ReturnType.INTEGER, List.of(reduceReqDTO.getKey()));
            if (result == 1) {
                return ReduceProto.ReduceRespDTO.newBuilder()
                        .setKey(reduceReqDTO.getKey())
                        .setSuccess(true)
                        .build();
            } else {
                return ReduceProto.ReduceRespDTO.newBuilder()
                        .setKey(reduceReqDTO.getKey())
                        .setSuccess(false)
                        .build();
            }
        });
    }

}
