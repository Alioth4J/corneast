package com.alioth4j.corneast_core.strategy;

import com.alioth4j.corneast_core.proto.RequestProto;
import com.alioth4j.corneast_core.proto.ResponseProto;

import java.util.concurrent.CompletableFuture;

/**
 * Strategy interface for different kinds of requests.
 *
 * Strategies are hold by {@link com.alioth4j.corneast_core.netty.RequestRouteHandler}
 *
 * @author Alioth Null
 */
public interface RequestHandlingStrategy {

    CompletableFuture<ResponseProto.ResponseDTO> handle(RequestProto.RequestDTO requestDTO);

}
