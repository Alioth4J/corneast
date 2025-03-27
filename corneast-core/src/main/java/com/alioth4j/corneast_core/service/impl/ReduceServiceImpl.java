package com.alioth4j.corneast_core.service.impl;

import com.alioth4j.corneast_core.pojo.ReduceReqDTO;
import com.alioth4j.corneast_core.pojo.ReduceRespDTO;
import com.alioth4j.corneast_core.service.ReduceService;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
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
                                            local current = redis.call('GET', KEYS[1])
                                            if not current then
                                                return 0
                                            end
                                            current = tonumber(current)
                                            if current > 0 then
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
    public CompletableFuture<ReduceRespDTO> reduce(ReduceReqDTO reduceReqDTO) {
        return CompletableFuture.supplyAsync(() -> {
            // pick a redissonClient randomly
            Random random = new Random();
            int nodeSize = redissonClients.size();
            RedissonClient redissonClient = redissonClients.get(random.nextInt(nodeSize));
            long result = redissonClient.getScript().eval(RScript.Mode.READ_WRITE, luaScript, RScript.ReturnType.INTEGER, List.of(reduceReqDTO.getKey()));
            if (result == 1) {
                return new ReduceRespDTO(reduceReqDTO.getKey(), Boolean.TRUE);
            } else {
                return new ReduceRespDTO(reduceReqDTO.getKey(), Boolean.FALSE);
            }
        });
    }

}
