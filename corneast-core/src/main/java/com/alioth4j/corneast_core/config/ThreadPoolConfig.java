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

    @Bean(value = "releaseExecutor")
    public Executor releaseExecutor() {
        return new ThreadPoolExecutor(
                20,
                40,
                10,
                TimeUnit.SECONDS,
                new SynchronousQueue<>(),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

}
