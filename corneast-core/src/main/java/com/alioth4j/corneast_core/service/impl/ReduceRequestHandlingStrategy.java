package com.alioth4j.corneast_core.service.impl;

import com.alioth4j.corneast_core.proto.RequestProto;
import com.alioth4j.corneast_core.proto.ResponseProto;
import com.alioth4j.corneast_core.service.RequestHandlingStrategy;
import jakarta.annotation.PostConstruct;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

@Component("reduce")
public class ReduceRequestHandlingStrategy implements RequestHandlingStrategy {

    @Autowired
    private List<RedissonClient> redissonClients;

    private int nodeSize;

    @PostConstruct
    public void init() {
        this.nodeSize = redissonClients.size();
    }

    private static final Random random = new Random();

    // reused objects
    private static final ResponseProto.ResponseDTO.Builder responseBuilder = ResponseProto.ResponseDTO.newBuilder().setType("reduce");
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
    @Async("reduceExecutor")
    public CompletableFuture<ResponseProto.ResponseDTO> handle(RequestProto.RequestDTO requestDTO) {
        return CompletableFuture.supplyAsync(() -> {
            // pick a redissonClient randomly
            RedissonClient redissonClient = redissonClients.get(random.nextInt(nodeSize));
            long result = redissonClient.getScript().eval(RScript.Mode.READ_WRITE, luaScript, RScript.ReturnType.INTEGER, List.of(requestDTO.getReduceReqDTO().getKey()));
            if (result == 1) {
                return responseBuilder
                       .setReduceRespDTO(successReduceRespBuilder
                                         .setKey(requestDTO.getReduceReqDTO().getKey())
                                         .build())
                       .build();
            } else {
                return responseBuilder
                       .setReduceRespDTO(failReduceRespBuilder
                                         .setKey(requestDTO.getReduceReqDTO().getKey())
                                         .build())
                       .build();
            }
        });
    }

}
