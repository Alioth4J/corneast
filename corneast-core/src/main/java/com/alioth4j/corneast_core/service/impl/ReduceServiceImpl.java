package com.alioth4j.corneast_core.service.impl;

import com.alioth4j.corneast_core.pojo.ReduceReqDTO;
import com.alioth4j.corneast_core.pojo.ReduceRespDTO;
import com.alioth4j.corneast_core.service.ReduceService;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
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
    
    @Autowired
    private List<RedissonClient> redissonClients;

    @Async
    @Override
    public CompletableFuture<ReduceRespDTO> reduce(ReduceReqDTO reduceReqDTO) {
        return CompletableFuture.supplyAsync(() -> {
            // pick a redissonClient randomly
            Random random = new Random();
            int nodeSize = redissonClients.size();
            RedissonClient redissonClient = redissonClients.get(random.nextInt(nodeSize));
            RBucket<Integer> bucket = redissonClient.getBucket(reduceReqDTO.getKey());
            // get lock
            RLock lock = redissonClient.getLock("lock:reduce:" + reduceReqDTO.getKey());
            try {
                // TODO adjust waitTime and leaseTime
                if (lock.tryLock(1000, 500, TimeUnit.MILLISECONDS)) {
                    try {
                        int remainingTokenCount = bucket.get();
                        if (remainingTokenCount > 0) {
                            bucket.set(remainingTokenCount - 1);
                            return new ReduceRespDTO(reduceReqDTO.getKey(), Boolean.TRUE);
                        } else {
                            return new ReduceRespDTO(reduceReqDTO.getKey(), Boolean.FALSE);
                        }
                    } finally {
                        lock.unlock();
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return new ReduceRespDTO(reduceReqDTO.getKey(), Boolean.FALSE);
        });
    }

}
