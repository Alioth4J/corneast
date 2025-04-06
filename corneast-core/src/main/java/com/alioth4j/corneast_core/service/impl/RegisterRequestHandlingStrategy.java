package com.alioth4j.corneast_core.service.impl;

import com.alioth4j.corneast_core.proto.RequestProto;
import com.alioth4j.corneast_core.proto.ResponseProto;
import com.alioth4j.corneast_core.service.RequestHandlingStrategy;
import jakarta.annotation.PostConstruct;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component("register")
public class RegisterRequestHandlingStrategy implements RequestHandlingStrategy {

    @Autowired
    private List<RedissonClient> redissonClients;

    private int nodeSize;

    @PostConstruct
    public void init() {
        this.nodeSize = redissonClients.size();
    }

    private static final ResponseProto.ResponseDTO.Builder responseBuilder = ResponseProto.ResponseDTO.newBuilder().setType("register");
    private static final ResponseProto.RegisterRespDTO.Builder successRespBuilder = ResponseProto.RegisterRespDTO.newBuilder().setSuccess(true);
    private static final ResponseProto.RegisterRespDTO.Builder failRespBuilder = ResponseProto.RegisterRespDTO.newBuilder().setSuccess(false);

    private static final String luaScript = """
                                            redis.call('SET', KEYS[1], ARGV[1])
                                            return 1
                                            """;

    // TODO use custom thread pool
    @Override
    @Async
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
                   .setRegisterRespDTO(successRespBuilder
                                       .setKey(requestDTO.getRegisterReqDTO().getKey())
                                       .build())
                   .build();
        });
    }

}
