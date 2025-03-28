package com.alioth4j.corneast_core.service.impl;

import com.alioth4j.corneast_core.proto.QueryProto;
import com.alioth4j.corneast_core.service.QueryService;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class QueryServiceImpl implements QueryService {

    private static final String luaScript = """
                                            local current = redis.call('GET', KEYS[1])
                                            return tonumber(current)
                                            """;

    @Autowired
    private List<RedissonClient> redissonClients;

    @Async
    @Override
    public CompletableFuture<QueryProto.QueryRespDTO> query(QueryProto.QueryReqDTO queryReqDTO) {
        return CompletableFuture.supplyAsync(() -> {
            // sum tokenCount from each node
            String key = queryReqDTO.getKey();
            long totalTokenCount = 0;
            for (RedissonClient redissonClient : redissonClients) {
                totalTokenCount += (long) redissonClient.getScript().eval(RScript.Mode.READ_ONLY, luaScript, RScript.ReturnType.INTEGER, List.of(key));
            }
            return QueryProto.QueryRespDTO.newBuilder()
                    .setKey(key)
                    .setRemainingTokenCount(totalTokenCount)
                    .build();
        });
    }

}
