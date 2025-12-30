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

package com.alioth4j.corneast.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.concurrent.TimeUnit;

/**
 * POJO of <code>threadPool</code> in application.yml.
 *
 * @author Alioth Null
 */
@ConfigurationProperties("threadpool")
public class ThreadPoolConfigProperties {

    private SingleThreadPoolConfigProperties register;
    private SingleThreadPoolConfigProperties reduce;
    private SingleThreadPoolConfigProperties release;
    private SingleThreadPoolConfigProperties query;

    public ThreadPoolConfigProperties() {
    }

    public SingleThreadPoolConfigProperties getRegister() {
        return register;
    }

    public void setRegister(SingleThreadPoolConfigProperties register) {
        this.register = register;
    }

    public SingleThreadPoolConfigProperties getReduce() {
        return reduce;
    }

    public void setReduce(SingleThreadPoolConfigProperties reduce) {
        this.reduce = reduce;
    }

    public SingleThreadPoolConfigProperties getRelease() {
        return release;
    }

    public void setRelease(SingleThreadPoolConfigProperties release) {
        this.release = release;
    }

    public SingleThreadPoolConfigProperties getQuery() {
        return query;
    }

    public void setQuery(SingleThreadPoolConfigProperties query) {
        this.query = query;
    }

    /**
     * POJO of a single thread pool configuration.
     */
    public static class SingleThreadPoolConfigProperties {

        private int corePoolSize;
        private int maximumPoolSize;
        private long keepAliveTime;
        private TimeUnit unit;

        public SingleThreadPoolConfigProperties() {
        }

        public int getCorePoolSize() {
            return corePoolSize;
        }

        public void setCorePoolSize(int corePoolSize) {
            this.corePoolSize = corePoolSize;
        }

        public int getMaximumPoolSize() {
            return maximumPoolSize;
        }

        public void setMaximumPoolSize(int maximumPoolSize) {
            this.maximumPoolSize = maximumPoolSize;
        }

        public long getKeepAliveTime() {
            return keepAliveTime;
        }

        public void setKeepAliveTime(long keepAliveTime) {
            this.keepAliveTime = keepAliveTime;
        }

        public TimeUnit getUnit() {
            return unit;
        }

        public void setUnit(TimeUnit unit) {
            this.unit = unit;
        }

    }

}
