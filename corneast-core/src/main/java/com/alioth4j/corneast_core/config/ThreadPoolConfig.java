package com.alioth4j.corneast_core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.*;

/**
 * Custom thread pool used by async methods.
 *
 * Adjust parameters according to your machines.
 *
 * @author Alioth Null
 */
@Configuration
@EnableAsync
public class ThreadPoolConfig {

    @Bean(value = "reduceExecutor")
    public Executor reduceExecutor() {
        return new ThreadPoolExecutor(
                1000,
                1000,
                10,
                TimeUnit.SECONDS,
                new SynchronousQueue<>(),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

    @Bean(value = "registerExecutor")
    public Executor registerExecutor() {
        return new ThreadPoolExecutor(
                1,
                10,
                10,
                TimeUnit.SECONDS,
                new SynchronousQueue<>(),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

    @Bean(value = "queryExecutor")
    public Executor queryExecutor() {
        return new ThreadPoolExecutor(
                1,
                10,
                10,
                TimeUnit.SECONDS,
                new SynchronousQueue<>(),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

}
