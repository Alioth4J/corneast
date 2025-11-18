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

package com.alioth4j.corneast_core.shutdown;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Shutdown task of thread pools.
 *
 * @author Alioth Null
 */
@Component
public class ThreadPoolShutdownTask implements ShutdownTask {

    private static final Logger log = LoggerFactory.getLogger(ThreadPoolShutdownTask.class);

    @Autowired(required = false)
    @Qualifier("registerExecutor")
    private Executor registerExecutor;

    @Autowired(required = false)
    @Qualifier("reduceExecutor")
    private Executor reduceExecutor;

    @Autowired(required = false)
    @Qualifier("releaseExecutor")
    private Executor releaseExecutor;

    @Autowired(required = false)
    @Qualifier("queryExecutor")
    private Executor queryExecutor;

    /* used for get bean name */
    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void shutdown() {
        shutdownExecutor(registerExecutor);
        shutdownExecutor(reduceExecutor);
        shutdownExecutor(releaseExecutor);
        shutdownExecutor(queryExecutor);
    }

    /**
     * Shutdown designated thread pool.
     * @param executor thread pool to be shutdown
     */
    private void shutdownExecutor(Executor executor) {
        if (executor instanceof ExecutorService executorService) {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                    if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                        log.warn("Error shutting down {}", getExecutorBeanName(executorService));
                    }
                }
            } catch (InterruptedException e) {
                log.warn("Interrupted when shutting down {}", getExecutorBeanName(executorService));
                Thread.currentThread().interrupt();
                executorService.shutdownNow();
            }
        }
    }

    /**
     * Gets the bean name of an executor.
     * Unable to find returns "unknownExecutor".
     * @param executor bean
     * @return bean name
     */
    private String getExecutorBeanName(Executor executor) {
        String[] beanNames = applicationContext.getBeanNamesForType(Executor.class);
        String name = Arrays.stream(beanNames).filter(beanName -> executor == applicationContext.getBean(beanName)).findFirst().orElse("unknownExecutor");
        return name;
    }

    @Override
    public String getComponentName() {
        return "ThreadPools";
    }

}
