package com.alioth4j.corneast_core.ringbuffer;

import com.alioth4j.corneast_core.proto.ResponseProto;
import com.lmax.disruptor.WorkHandler;
import jakarta.annotation.PostConstruct;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Scope("prototype")
public class ReduceWorkHandler implements WorkHandler<ReduceEvent>  {

    @Autowired
    private List<RedissonClient> redissonClients;

    private int nodeSize;

    @PostConstruct
    public void init() {
        this.nodeSize = redissonClients.size();
    }

    private static final Map<String, ResponseProto.ResponseDTO> cachedSuccessResponses = new ConcurrentHashMap<>();
    private static final Map<String, ResponseProto.ResponseDTO> cachedFailResponses = new ConcurrentHashMap<>();

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
                        .build());
            }
            responseDTO = cachedSuccessResponses.get(key);
        } else {
            if (!cachedFailResponses.containsKey(key)) {
                cachedFailResponses.put(key, responseBuilder
                        .setReduceRespDTO(failReduceRespBuilder
                                .setKey(key)
                                .build())
                        .build());
            }
            responseDTO = cachedFailResponses.get(key);
        }
        future.complete(responseDTO);
    }

}
