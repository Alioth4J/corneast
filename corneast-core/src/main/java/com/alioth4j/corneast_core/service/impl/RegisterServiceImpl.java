package com.alioth4j.corneast_core.service.impl;

import com.alioth4j.corneast_core.pojo.RegisterReqDTO;
import com.alioth4j.corneast_core.pojo.RegisterRespDTO;
import com.alioth4j.corneast_core.service.RegisterService;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RegisterServiceImpl implements RegisterService {

    private static final String luaScript = """
                                            redis.call('SET', KEYS[1], ARGV[1])
                                            return 1
                                            """;

    @Autowired
    private List<RedissonClient> redissonClients;

    @Override
    public RegisterRespDTO register(RegisterReqDTO registerReqDTO) {
        // distribute tokenCount to all the nodes evenly
        String key = registerReqDTO.getKey();
        long totalTokenCount = registerReqDTO.getTokenCount();
        int nodeSize = redissonClients.size();
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
        return new RegisterRespDTO(registerReqDTO.getKey(), Boolean.TRUE);
    }

}
