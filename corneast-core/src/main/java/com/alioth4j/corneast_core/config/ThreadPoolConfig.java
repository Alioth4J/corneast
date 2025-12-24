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

package com.alioth4j.corneast_core.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.*;

/**
 * Custom thread pool used by async methods.
 *
 * @author Alioth Null
 */
@Configuration
@EnableConfigurationProperties(ThreadPoolConfigProperties.class)
@EnableAsync
public class ThreadPoolConfig {

    @Bean(value = "reduceExecutor")
    public Executor reduceExecutor(ThreadPoolConfigProperties configProperties) {
        ThreadPoolConfigProperties.SingleThreadPoolConfigProperties reduceConfigProperties = configProperties.getReduce();
        return new ThreadPoolExecutor(
                reduceConfigProperties.getCorePoolSize(),
                reduceConfigProperties.getMaximumPoolSize(),
                reduceConfigProperties.getKeepAliveTime(),
                reduceConfigProperties.getUnit(),
                new SynchronousQueue<>(),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

    @Bean(value = "registerExecutor")
    public Executor registerExecutor(ThreadPoolConfigProperties configProperties) {
        ThreadPoolConfigProperties.SingleThreadPoolConfigProperties registerConfigProperties = configProperties.getRegister();
        return new ThreadPoolExecutor(
                registerConfigProperties.getCorePoolSize(),
                registerConfigProperties.getMaximumPoolSize(),
                registerConfigProperties.getKeepAliveTime(),
                registerConfigProperties.getUnit(),
                new SynchronousQueue<>(),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

    @Bean(value = "queryExecutor")
    public Executor queryExecutor(ThreadPoolConfigProperties configProperties) {
        ThreadPoolConfigProperties.SingleThreadPoolConfigProperties queryConfigProperties = configProperties.getQuery();
        return new ThreadPoolExecutor(
                queryConfigProperties.getCorePoolSize(),
                queryConfigProperties.getMaximumPoolSize(),
                queryConfigProperties.getKeepAliveTime(),
                queryConfigProperties.getUnit(),
                new SynchronousQueue<>(),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

    @Bean(value = "releaseExecutor")
    public Executor releaseExecutor(ThreadPoolConfigProperties configProperties) {
        ThreadPoolConfigProperties.SingleThreadPoolConfigProperties releaseConfigProperties = configProperties.getRelease();
        return new ThreadPoolExecutor(
                releaseConfigProperties.getCorePoolSize(),
                releaseConfigProperties.getMaximumPoolSize(),
                releaseConfigProperties.getKeepAliveTime(),
                releaseConfigProperties.getUnit(),
                new SynchronousQueue<>(),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

}
