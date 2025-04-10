package com.alioth4j.corneast_core.ringbuffer;

import com.alioth4j.corneast_core.proto.RequestProto;
import com.alioth4j.corneast_core.proto.ResponseProto;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class ReduceDisruptor {

    private Disruptor<ReduceEvent> disruptor;
    private RingBuffer<ReduceEvent> ringBuffer;

    @Autowired
    private ReduceEventHandler reduceEventHandler;

    private static final int BUFFER_SIZE = 1024 * 1024;

    private ExecutorService executor;

    @PostConstruct
    public void init() {
        this.executor = Executors.newCachedThreadPool();
        ReduceEventFactory factory = new ReduceEventFactory();
        this.disruptor = new Disruptor<>(factory, BUFFER_SIZE, executor, ProducerType.MULTI, new BlockingWaitStrategy());
        disruptor.handleEventsWith(reduceEventHandler);
        disruptor.start();
        this.ringBuffer = disruptor.getRingBuffer();
    }

    public CompletableFuture<ResponseProto.ResponseDTO> submitRequest(RequestProto.RequestDTO requestDTO) {
        long sequence = ringBuffer.next();
        ReduceEvent event = ringBuffer.get(sequence);
        event.setKey(requestDTO.getReduceReqDTO().getKey());
        CompletableFuture<ResponseProto.ResponseDTO> future = new CompletableFuture<>();
        event.setFuture(future);
        ringBuffer.publish(sequence);
        return future;
    }

    @PreDestroy
    public void shutdown() {
        if (disruptor != null) {
            disruptor.shutdown();
        }
        if (executor != null) {
            executor.shutdown();
        }
    }

}
