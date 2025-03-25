package com.alioth4j.corneast_core.service.impl;

import com.alioth4j.corneast_core.pojo.RegisterReqDTO;
import com.alioth4j.corneast_core.pojo.RegisterRespDTO;
import com.alioth4j.corneast_core.service.RegisterService;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RegisterServiceImpl implements RegisterService {

    @Autowired
    private List<RedissonClient> redissonClients;

    @Override
    public RegisterRespDTO register(RegisterReqDTO registerReqDTO) {
        // distribute tokenCount to all the nodes evenly
        String key = registerReqDTO.getKey();
        int totalTokenCount = registerReqDTO.getTokenCount();
        int nodeSize = redissonClients.size();
        int averageTokenCount = totalTokenCount / nodeSize;
        int remainingTokenCount = totalTokenCount % nodeSize;
        for (int i = 0; i < nodeSize; i++) {
            RedissonClient redissonClient = redissonClients.get(i);
            RBucket<Integer> bucket = redissonClient.getBucket(key);
            if (remainingTokenCount > 0) {
                bucket.set(averageTokenCount + 1);
                remainingTokenCount--;
            } else {
                bucket.set(averageTokenCount);
            }
        }
        return new RegisterRespDTO(registerReqDTO.getKey(), Boolean.TRUE);
    }

}
