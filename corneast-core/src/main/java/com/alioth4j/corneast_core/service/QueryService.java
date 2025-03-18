package com.alioth4j.corneast_core.service;

import com.alioth4j.corneast_core.pojo.QueryReqDTO;
import com.alioth4j.corneast_core.pojo.QueryRespDTO;

import java.util.concurrent.CompletableFuture;

public interface QueryService {

    CompletableFuture<QueryRespDTO> query(QueryReqDTO queryReqDTO);

}
