package com.alioth4j.corneast_core.service;

import com.alioth4j.corneast_core.pojo.ReduceReqDTO;
import com.alioth4j.corneast_core.pojo.ReduceRespDTO;

import java.util.concurrent.CompletableFuture;

public interface ReduceService {

    CompletableFuture<ReduceRespDTO> reduce(ReduceReqDTO reduceReqDTO);

}
