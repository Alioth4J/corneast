package com.alioth4j.corneast_core.service.impl;

import com.alioth4j.corneast_core.pojo.ReduceReqDTO;
import com.alioth4j.corneast_core.pojo.ReduceRespDTO;
import com.alioth4j.corneast_core.service.ReduceService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class ReduceServiceImpl implements ReduceService {

    @Async
    @Override
    public CompletableFuture<ReduceRespDTO> reduce(ReduceReqDTO reduceReqDTO) {
        return null;
    }

}
