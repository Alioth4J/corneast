package com.alioth4j.corneast_core.service.impl;

import com.alioth4j.corneast_core.proto.ReduceProto;
import com.alioth4j.corneast_core.service.ReduceService;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

@Deprecated
@Service
public class ReduceServiceImpl implements ReduceService {

    private List<RedissonClient> redissonClients;

    private int nodeSize;

    public ReduceServiceImpl(List<RedissonClient> redissonClients) {
        this.redissonClients = redissonClients;
        this.nodeSize = redissonClients.size();
    }

    private static final Random random = new Random();

    private static final ReduceProto.ReduceRespDTO.Builder successRespBuilder = ReduceProto.ReduceRespDTO.newBuilder().setSuccess(true);

    private static final ReduceProto.ReduceRespDTO.Builder failRespBuilder = ReduceProto.ReduceRespDTO.newBuilder().setSuccess(false);

    private static final String luaScript = """
                                            local n = tonumber(redis.call('GET', KEYS[1]) or "0")
                                            if n > 0 then
                                                redis.call('DECR', KEYS[1])
                                                return 1
                                            else 
                                                return 0
                                            end
                                            """;

    @Async("reduceExecutor")
    @Override
    public CompletableFuture<ReduceProto.ReduceRespDTO> reduce(ReduceProto.ReduceReqDTO reduceReqDTO) {
        return CompletableFuture.supplyAsync(() -> {
            // pick a redissonClient randomly
            RedissonClient redissonClient = redissonClients.get(random.nextInt(nodeSize));
            long result = redissonClient.getScript().eval(RScript.Mode.READ_WRITE, luaScript, RScript.ReturnType.INTEGER, List.of(reduceReqDTO.getKey()));
            if (result == 1) {
                return successRespBuilder
                        .setKey(reduceReqDTO.getKey())
                        .build();
            } else {
                return failRespBuilder
                        .setKey(reduceReqDTO.getKey())
                        .build();
            }
        });
    }

}
