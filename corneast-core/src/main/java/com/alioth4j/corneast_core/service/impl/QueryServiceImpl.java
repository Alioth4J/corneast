package com.alioth4j.corneast_core.service.impl;

import com.alioth4j.corneast_core.pojo.QueryReqDTO;
import com.alioth4j.corneast_core.pojo.QueryRespDTO;
import com.alioth4j.corneast_core.service.QueryService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class QueryServiceImpl implements QueryService {

    @Async
    @Override
    public CompletableFuture<QueryRespDTO> query(QueryReqDTO queryReqDTO) {
        return null;
    }

}
