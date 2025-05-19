package com.alioth4j.corneast_core.strategy.impl;

import com.alioth4j.corneast_core.proto.RequestProto;
import com.alioth4j.corneast_core.proto.ResponseProto;
import com.alioth4j.corneast_core.strategy.RequestHandlingStrategy;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * Query request handling strategy.
 *
 * @author Alioth Null
 */
@Component("query")
public class QueryRequestHandlingStrategy implements RequestHandlingStrategy {

    @Autowired
    @Qualifier("queryExecutor")
    private Executor queryExecutor;

    @Autowired
    @Qualifier("redissonClients")
    private List<RedissonClient> redissonClients;

    private static final ResponseProto.ResponseDTO.Builder responseBuilder = ResponseProto.ResponseDTO.newBuilder().setType("query");
    private static final ResponseProto.QueryRespDTO.Builder queryResponseBuilder = ResponseProto.QueryRespDTO.newBuilder();

    private static final String luaScript = """
                                            local current = redis.call('GET', KEYS[1])
                                            if current == nil then
                                                return 0
                                            else
                                                return tonumber(current)
                                            end
                                            """;

    @Override
    public CompletableFuture<ResponseProto.ResponseDTO> handle(RequestProto.RequestDTO requestDTO) {
        return CompletableFuture.supplyAsync(() -> {
            // sum tokenCount from each node
            String key = requestDTO.getQueryReqDTO().getKey();
            long totalTokenCount = 0;
            for (RedissonClient redissonClient : redissonClients) {
                totalTokenCount += (long) redissonClient.getScript().eval(RScript.Mode.READ_ONLY, luaScript, RScript.ReturnType.INTEGER, List.of(key));
            }
            return responseBuilder
                   .setQueryRespDTO(queryResponseBuilder
                                    .setKey(key)
                                    .setRemainingTokenCount(totalTokenCount)
                                    .build())
                   .build();
        }, queryExecutor);
    }

}
