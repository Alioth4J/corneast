package com.alioth4j.corneast_core.strategy.impl;

import com.alioth4j.corneast_core.proto.RequestProto;
import com.alioth4j.corneast_core.proto.ResponseProto;
import com.alioth4j.corneast_core.ringbuffer.ReduceDisruptor;
import com.alioth4j.corneast_core.strategy.RequestHandlingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * Reduce request handling strategy.
 *
 * @author Alioth Null
 */
@Component("reduce")
public class ReduceRequestHandlingStrategy implements RequestHandlingStrategy {

    @Autowired
    private ReduceDisruptor reduceDisruptor;

    @Override
    @Async("reduceExecutor")
    public CompletableFuture<ResponseProto.ResponseDTO> handle(RequestProto.RequestDTO requestDTO) {
        return reduceDisruptor.submitRequest(requestDTO);
    }

}
