package com.alioth4j.corneast_core.ringbuffer;

import com.alioth4j.corneast_core.proto.ResponseProto;

import java.util.concurrent.CompletableFuture;

/**
 * Used in RingBuffer.
 *
 * Stores the key and the CompletableFuture for the response.
 *
 * @author Alioth Null
 */
public class ReduceEvent {

    private String key;

    // for response
    private CompletableFuture<ResponseProto.ResponseDTO> future;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public CompletableFuture<ResponseProto.ResponseDTO> getFuture() {
        return future;
    }

    public void setFuture(CompletableFuture<ResponseProto.ResponseDTO> future) {
        this.future = future;
    }

    @Override
    public String toString() {
        return "ReduceEvent{" +
                "key='" + key + '\'' +
                ", future=" + future +
                '}';
    }

}
