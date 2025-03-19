package com.alioth4j.corneast_core.service.impl;

import com.alioth4j.corneast_core.pojo.QueryReqDTO;
import com.alioth4j.corneast_core.pojo.QueryRespDTO;
import com.alioth4j.corneast_core.service.QueryService;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class QueryServiceImpl implements QueryService {

    @Autowired
    private RedissonClient redissonClient;

    @Async
    @Override
    public CompletableFuture<QueryRespDTO> query(QueryReqDTO queryReqDTO) {
        return CompletableFuture.supplyAsync(() -> {
            RBucket<Integer> bucket = redissonClient.getBucket(queryReqDTO.getKey());
            return new QueryRespDTO(queryReqDTO.getKey(), bucket.get());
        });
    }

}
