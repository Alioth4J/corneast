package com.alioth4j.corneast_core.ringbuffer;

import com.alioth4j.corneast_core.proto.RequestProto;
import com.alioth4j.corneast_core.proto.ResponseProto;
import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class ReduceDisruptor {

    private Disruptor<ReduceEvent> disruptor;
    private RingBuffer<ReduceEvent> ringBuffer;
    private WorkerPool<ReduceEvent> workerPool;

    // In order to inject prototype beans of ReduceWorkHandler
    @Autowired
    private ApplicationContext applicationContext;

    private static final int BUFFER_SIZE = 1024 * 1024;

    private ExecutorService executor;

    @PostConstruct
    public void init() {
        executor = Executors.newCachedThreadPool();

        ReduceEventFactory factory = new ReduceEventFactory();
        disruptor = new Disruptor<>(factory, BUFFER_SIZE, executor, ProducerType.MULTI, new BlockingWaitStrategy());

        ringBuffer = disruptor.getRingBuffer();

        ExceptionHandler<ReduceEvent> exceptionHandler = new ExceptionHandler<>() {
            @Override
            public void handleEventException(Throwable throwable, long l, ReduceEvent reduceEvent) {
                throwable.printStackTrace();
            }

            @Override
            public void handleOnStartException(Throwable throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void handleOnShutdownException(Throwable throwable) {
                throwable.printStackTrace();
            }
        };
        List<WorkHandler<ReduceEvent>> workHandlerList = new ArrayList<>(1000);
        for (int i = 0; i < 1000; i++) {
            workHandlerList.add(applicationContext.getBean(ReduceWorkHandler.class));
        }
        workerPool = new WorkerPool<>(ringBuffer, ringBuffer.newBarrier(), exceptionHandler, workHandlerList.toArray(new WorkHandler[0]));
        ringBuffer.addGatingSequences(workerPool.getWorkerSequences());
//        disruptor.start();
        ringBuffer = workerPool.start(executor);
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
        if (workerPool != null) {
            workerPool.halt();
        }
        if (executor != null) {
            executor.shutdown();
        }
    }

}
