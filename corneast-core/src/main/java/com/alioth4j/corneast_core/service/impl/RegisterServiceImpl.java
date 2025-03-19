package com.alioth4j.corneast_core.service.impl;

import com.alioth4j.corneast_core.pojo.RegisterReqDTO;
import com.alioth4j.corneast_core.pojo.RegisterRespDTO;
import com.alioth4j.corneast_core.service.RegisterService;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegisterServiceImpl implements RegisterService {

    @Autowired
    private RedissonClient redissonClient;

    @Override
    public RegisterRespDTO register(RegisterReqDTO registerReqDTO) {
        RBucket<Integer> bucket = redissonClient.getBucket(registerReqDTO.getKey());
        bucket.set(registerReqDTO.getTokenCount());
        return new RegisterRespDTO(registerReqDTO.getKey(), Boolean.TRUE);
    }

}
