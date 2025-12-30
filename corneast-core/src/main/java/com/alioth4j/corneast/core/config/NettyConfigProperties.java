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

import io.netty.handler.logging.LogLevel;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "netty")
public class NettyConfigProperties {

    private int port = 8088;
    private String threadName = "Netty-Server";
    private boolean daemon = false;

    private BossGroup bossGroup;
    private WorkerGroup workerGroup;

    public NettyConfigProperties() {
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public boolean isDaemon() {
        return daemon;
    }

    public void setDaemon(boolean daemon) {
        this.daemon = daemon;
    }

    public BossGroup getBossGroup() {
        return bossGroup;
    }

    public void setBossGroup(BossGroup bossGroup) {
        this.bossGroup = bossGroup;
    }

    public WorkerGroup getWorkerGroup() {
        return workerGroup;
    }

    public void setWorkerGroup(WorkerGroup workerGroup) {
        this.workerGroup = workerGroup;
    }

    public static class BossGroup {

        private int nThreads = 1;
        private int soBacklog = 1024;
        private boolean soReuseAddr = true;
        private LogLevel logLevel = LogLevel.DEBUG;

        public BossGroup() {
        }

        public int getNThreads() {
            return nThreads;
        }

        public void setNThreads(int nThreads) {
            this.nThreads = nThreads;
        }

        public int getSoBacklog() {
            return soBacklog;
        }

        public void setSoBacklog(int soBacklog) {
            this.soBacklog = soBacklog;
        }

        public boolean isSoReuseAddr() {
            return soReuseAddr;
        }

        public void setSoReuseAddr(boolean soReuseAddr) {
            this.soReuseAddr = soReuseAddr;
        }

        public LogLevel getLogLevel() {
            return logLevel;
        }

        public void setLogLevel(LogLevel logLevel) {
            this.logLevel = logLevel;
        }

    }

    public static class WorkerGroup {

        private int nThreads = 100;
        private boolean keepAlive = false;
        private boolean tcpNodelay = true;
        private Allocator allocator;
        private RcvBufAllocator rcvBufAllocator;

        public WorkerGroup() {
        }

        public int getNThreads() {
            return nThreads;
        }

        public void setNThreads(int nThreads) {
            this.nThreads = nThreads;
        }

        public boolean isKeepAlive() {
            return keepAlive;
        }

        public void setKeepAlive(boolean keepAlive) {
            this.keepAlive = keepAlive;
        }

        public boolean isTcpNodelay() {
            return tcpNodelay;
        }

        public void setTcpNodelay(boolean tcpNodelay) {
            this.tcpNodelay = tcpNodelay;
        }

        public Allocator getAllocator() {
            return allocator;
        }

        public void setAllocator(Allocator allocator) {
            this.allocator = allocator;
        }

        public RcvBufAllocator getRcvBufAllocator() {
            return rcvBufAllocator;
        }

        public void setRcvBufAllocator(RcvBufAllocator rcvBufAllocator) {
            this.rcvBufAllocator = rcvBufAllocator;
        }

        public static class Allocator {

            private boolean preferDirect = true;
            private int nHeapArena = 40;
            private int nDirectArena = 40;
            private int pageSize = 8192;
            private int maxOrder = 11;

            public Allocator() {
            }

            public boolean isPreferDirect() {
                return preferDirect;
            }

            public void setPreferDirect(boolean preferDirect) {
                this.preferDirect = preferDirect;
            }

            public int getNHeapArena() {
                return nHeapArena;
            }

            public void setNHeapArena(int nHeapArena) {
                this.nHeapArena = nHeapArena;
            }

            public int getNDirectArena() {
                return nDirectArena;
            }

            public void setNDirectArena(int nDirectArena) {
                this.nDirectArena = nDirectArena;
            }

            public int getPageSize() {
                return pageSize;
            }

            public void setPageSize(int pageSize) {
                this.pageSize = pageSize;
            }

            public int getMaxOrder() {
                return maxOrder;
            }

            public void setMaxOrder(int maxOrder) {
                this.maxOrder = maxOrder;
            }

        }

        public static class RcvBufAllocator {

            private int minimum = 64;
            private int initial = 8192;
            private int maximum = 65535;

            public RcvBufAllocator() {
            }

            public int getMinimum() {
                return minimum;
            }

            public void setMinimum(int minimum) {
                this.minimum = minimum;
            }

            public int getInitial() {
                return initial;
            }

            public void setInitial(int initial) {
                this.initial = initial;
            }

            public int getMaximum() {
                return maximum;
            }

            public void setMaximum(int maximum) {
                this.maximum = maximum;
            }

        }

    }

}
