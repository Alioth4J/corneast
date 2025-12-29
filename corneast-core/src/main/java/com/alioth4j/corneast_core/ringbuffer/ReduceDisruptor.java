/*
 * Corneast
 * Copyright (C) 2025 Alioth Null
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.alioth4j.corneast_core.ringbuffer;

import com.alioth4j.corneast_common.proto.RequestProto;
import com.alioth4j.corneast_common.proto.ResponseProto;
import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * RingBuffer for reduce requests.
 *
 * @author Alioth Null
 */
@Component
public class ReduceDisruptor {

    private RingBuffer<ReduceEvent> ringBuffer;

    // In order to inject prototype beans of ReduceWorkHandler
    @Autowired
    private ApplicationContext applicationContext;

    private static final int BUFFER_SIZE = 1024 * 1024;

    private ExecutorService executor;

    @Value("${ringbuffer.workHandlerCount}")
    private int workHandlerCount;

    @PostConstruct
    public void init() {
        executor = Executors.newCachedThreadPool();

        ReduceEventFactory factory = new ReduceEventFactory();
        Disruptor<ReduceEvent> disruptor = new Disruptor<>(factory, BUFFER_SIZE, executor, ProducerType.MULTI, new BlockingWaitStrategy());

        ringBuffer = disruptor.getRingBuffer();

        ExceptionHandler<ReduceEvent> exceptionHandler = new ExceptionHandler<>() {

            private static final Logger log = LoggerFactory.getLogger(ReduceDisruptor.class.getName() + "$" + ExceptionHandler.class.getSimpleName());

            @Override
            public void handleEventException(Throwable throwable, long sequence, ReduceEvent reduceEvent) {
                log.error("Error handling event [{}] at sequence [{}]", reduceEvent, sequence, throwable);
            }

            @Override
            public void handleOnStartException(Throwable throwable) {
                log.error("Error starting worker pool of disruptor", throwable);
            }

            @Override
            public void handleOnShutdownException(Throwable throwable) {
                log.error("Error shutting down worker pool of disruptor", throwable);
            }
        };

        @SuppressWarnings("unchecked")
        WorkHandler<ReduceEvent>[] workHandlers = new WorkHandler[workHandlerCount];
        for (int i = 0; i < workHandlerCount; i++) {
            workHandlers[i] = applicationContext.getBean(ReduceWorkHandler.class);
        }
        WorkerPool<ReduceEvent> workerPool = new WorkerPool<>(ringBuffer, ringBuffer.newBarrier(), exceptionHandler, workHandlers);
        ringBuffer.addGatingSequences(workerPool.getWorkerSequences());
//        disruptor.start();
        ringBuffer = workerPool.start(executor);
    }

    public CompletableFuture<ResponseProto.ResponseDTO> submitRequest(RequestProto.RequestDTO requestDTO) {
        long sequence = ringBuffer.next();
        ReduceEvent event = ringBuffer.get(sequence);
        event.setId(requestDTO.getId());
        event.setKey(requestDTO.getReduceReqDTO().getKey());
        CompletableFuture<ResponseProto.ResponseDTO> future = new CompletableFuture<>();
        event.setFuture(future);
        ringBuffer.publish(sequence);
        return future;
    }

    /**
     * Shutdown disruptor.
     * @param log Logger object to use
     */
    public void shutdown(Logger log) {
        // shutdown executor
        if (executor != null && !executor.isShutdown()) {
            log.info("Shutting down executor in disruptor");
            executor.shutdown();
        }
    }

}
