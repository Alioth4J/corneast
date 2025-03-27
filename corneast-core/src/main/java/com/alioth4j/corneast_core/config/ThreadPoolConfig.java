package com.alioth4j.corneast_core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.*;

@Configuration
@EnableAsync
public class ThreadPoolConfig {

    @Bean(value = "reduceExecutor")
    public Executor reduceExecutor() {
        return new ThreadPoolExecutor(
                40,
                40,
                10,
                TimeUnit.SECONDS,
                // TODO queue size
                new LinkedBlockingQueue<>(),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

}
