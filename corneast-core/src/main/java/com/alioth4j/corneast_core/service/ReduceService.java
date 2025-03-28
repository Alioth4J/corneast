package com.alioth4j.corneast_core.service;

import com.alioth4j.corneast_core.proto.ReduceProto;

import java.util.concurrent.CompletableFuture;

public interface ReduceService {

    CompletableFuture<ReduceProto.ReduceRespDTO> reduce(ReduceProto.ReduceReqDTO reduceReqDTO);

}
