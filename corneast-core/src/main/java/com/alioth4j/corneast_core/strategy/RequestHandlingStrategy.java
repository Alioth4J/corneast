package com.alioth4j.corneast_core.strategy;

import com.alioth4j.corneast_core.proto.RequestProto;
import com.alioth4j.corneast_core.proto.ResponseProto;

import java.util.concurrent.CompletableFuture;

public interface RequestHandlingStrategy {

    CompletableFuture<ResponseProto.ResponseDTO> handle(RequestProto.RequestDTO requestDTO);

}
