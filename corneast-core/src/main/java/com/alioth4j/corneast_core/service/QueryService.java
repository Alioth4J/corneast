package com.alioth4j.corneast_core.service;

import com.alioth4j.corneast_core.proto.QueryProto;

import java.util.concurrent.CompletableFuture;

@Deprecated
public interface QueryService {

    CompletableFuture<QueryProto.QueryRespDTO> query(QueryProto.QueryReqDTO queryReqDTO);

}
