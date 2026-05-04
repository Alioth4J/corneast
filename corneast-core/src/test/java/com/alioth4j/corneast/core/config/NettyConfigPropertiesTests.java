/*
 * Corneast
 * Copyright (C) 2026 Alioth Null
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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class NettyConfigPropertiesTests {

    // ---- Top-level properties ----

    @Test
    void testDefaultValues() {
        NettyConfigProperties props = new NettyConfigProperties();
        Assertions.assertEquals(8088, props.getPort());
        Assertions.assertEquals("Netty-Server", props.getThreadName());
        Assertions.assertFalse(props.isDaemon());
    }

    @Test
    void testSetAndGetPort() {
        NettyConfigProperties props = new NettyConfigProperties();
        props.setPort(9090);
        Assertions.assertEquals(9090, props.getPort());
    }

    @Test
    void testSetAndGetThreadName() {
        NettyConfigProperties props = new NettyConfigProperties();
        props.setThreadName("Custom-Thread");
        Assertions.assertEquals("Custom-Thread", props.getThreadName());
    }

    @Test
    void testSetAndGetDaemon() {
        NettyConfigProperties props = new NettyConfigProperties();
        props.setDaemon(true);
        Assertions.assertTrue(props.isDaemon());
    }

    @Test
    void testSetAndGetBossGroup() {
        NettyConfigProperties props = new NettyConfigProperties();
        NettyConfigProperties.BossGroup bossGroup = new NettyConfigProperties.BossGroup();
        props.setBossGroup(bossGroup);
        Assertions.assertSame(bossGroup, props.getBossGroup());
    }

    @Test
    void testSetAndGetWorkerGroup() {
        NettyConfigProperties props = new NettyConfigProperties();
        NettyConfigProperties.WorkerGroup workerGroup = new NettyConfigProperties.WorkerGroup();
        props.setWorkerGroup(workerGroup);
        Assertions.assertSame(workerGroup, props.getWorkerGroup());
    }

    // ---- BossGroup ----

    @Test
    void testBossGroupDefaults() {
        NettyConfigProperties.BossGroup bossGroup = new NettyConfigProperties.BossGroup();
        Assertions.assertEquals(1, bossGroup.getNThreads());
        Assertions.assertEquals(1024, bossGroup.getSoBacklog());
        Assertions.assertTrue(bossGroup.isSoReuseAddr());
        Assertions.assertEquals(LogLevel.DEBUG, bossGroup.getLogLevel());
    }

    @Test
    void testBossGroupSetAndGetNThreads() {
        NettyConfigProperties.BossGroup bossGroup = new NettyConfigProperties.BossGroup();
        bossGroup.setNThreads(4);
        Assertions.assertEquals(4, bossGroup.getNThreads());
    }

    @Test
    void testBossGroupSetAndGetSoBacklog() {
        NettyConfigProperties.BossGroup bossGroup = new NettyConfigProperties.BossGroup();
        bossGroup.setSoBacklog(2048);
        Assertions.assertEquals(2048, bossGroup.getSoBacklog());
    }

    @Test
    void testBossGroupSetAndGetSoReuseAddr() {
        NettyConfigProperties.BossGroup bossGroup = new NettyConfigProperties.BossGroup();
        bossGroup.setSoReuseAddr(false);
        Assertions.assertFalse(bossGroup.isSoReuseAddr());
    }

    @Test
    void testBossGroupSetAndGetLogLevel() {
        NettyConfigProperties.BossGroup bossGroup = new NettyConfigProperties.BossGroup();
        bossGroup.setLogLevel(LogLevel.INFO);
        Assertions.assertEquals(LogLevel.INFO, bossGroup.getLogLevel());
    }

    // ---- WorkerGroup ----

    @Test
    void testWorkerGroupDefaults() {
        NettyConfigProperties.WorkerGroup workerGroup = new NettyConfigProperties.WorkerGroup();
        Assertions.assertEquals(100, workerGroup.getNThreads());
        Assertions.assertFalse(workerGroup.isKeepAlive());
        Assertions.assertTrue(workerGroup.isTcpNodelay());
    }

    @Test
    void testWorkerGroupSetAndGetNThreads() {
        NettyConfigProperties.WorkerGroup workerGroup = new NettyConfigProperties.WorkerGroup();
        workerGroup.setNThreads(80);
        Assertions.assertEquals(80, workerGroup.getNThreads());
    }

    @Test
    void testWorkerGroupSetAndGetKeepAlive() {
        NettyConfigProperties.WorkerGroup workerGroup = new NettyConfigProperties.WorkerGroup();
        workerGroup.setKeepAlive(true);
        Assertions.assertTrue(workerGroup.isKeepAlive());
    }

    @Test
    void testWorkerGroupSetAndGetTcpNodelay() {
        NettyConfigProperties.WorkerGroup workerGroup = new NettyConfigProperties.WorkerGroup();
        workerGroup.setTcpNodelay(false);
        Assertions.assertFalse(workerGroup.isTcpNodelay());
    }

    @Test
    void testWorkerGroupSetAndGetAllocator() {
        NettyConfigProperties.WorkerGroup workerGroup = new NettyConfigProperties.WorkerGroup();
        NettyConfigProperties.WorkerGroup.Allocator allocator = new NettyConfigProperties.WorkerGroup.Allocator();
        workerGroup.setAllocator(allocator);
        Assertions.assertSame(allocator, workerGroup.getAllocator());
    }

    @Test
    void testWorkerGroupSetAndGetRcvBufAllocator() {
        NettyConfigProperties.WorkerGroup workerGroup = new NettyConfigProperties.WorkerGroup();
        NettyConfigProperties.WorkerGroup.RcvBufAllocator rcvBufAllocator = new NettyConfigProperties.WorkerGroup.RcvBufAllocator();
        workerGroup.setRcvBufAllocator(rcvBufAllocator);
        Assertions.assertSame(rcvBufAllocator, workerGroup.getRcvBufAllocator());
    }

    @Test
    void testWorkerGroupSetAndGetWriteBufferWaterMark() {
        NettyConfigProperties.WorkerGroup workerGroup = new NettyConfigProperties.WorkerGroup();
        NettyConfigProperties.WorkerGroup.WriteBufferWaterMark mark = new NettyConfigProperties.WorkerGroup.WriteBufferWaterMark();
        workerGroup.setWriteBufferWaterMark(mark);
        Assertions.assertSame(mark, workerGroup.getWriteBufferWaterMark());
    }

    // ---- Allocator ----

    @Test
    void testAllocatorDefaults() {
        NettyConfigProperties.WorkerGroup.Allocator allocator = new NettyConfigProperties.WorkerGroup.Allocator();
        Assertions.assertTrue(allocator.isPreferDirect());
        Assertions.assertEquals(40, allocator.getNHeapArena());
        Assertions.assertEquals(40, allocator.getNDirectArena());
        Assertions.assertEquals(8192, allocator.getPageSize());
        Assertions.assertEquals(11, allocator.getMaxOrder());
    }

    @Test
    void testAllocatorSetAndGetPreferDirect() {
        NettyConfigProperties.WorkerGroup.Allocator allocator = new NettyConfigProperties.WorkerGroup.Allocator();
        allocator.setPreferDirect(false);
        Assertions.assertFalse(allocator.isPreferDirect());
    }

    @Test
    void testAllocatorSetAndGetNHeapArena() {
        NettyConfigProperties.WorkerGroup.Allocator allocator = new NettyConfigProperties.WorkerGroup.Allocator();
        allocator.setNHeapArena(20);
        Assertions.assertEquals(20, allocator.getNHeapArena());
    }

    @Test
    void testAllocatorSetAndGetNDirectArena() {
        NettyConfigProperties.WorkerGroup.Allocator allocator = new NettyConfigProperties.WorkerGroup.Allocator();
        allocator.setNDirectArena(30);
        Assertions.assertEquals(30, allocator.getNDirectArena());
    }

    @Test
    void testAllocatorSetAndGetPageSize() {
        NettyConfigProperties.WorkerGroup.Allocator allocator = new NettyConfigProperties.WorkerGroup.Allocator();
        allocator.setPageSize(4096);
        Assertions.assertEquals(4096, allocator.getPageSize());
    }

    @Test
    void testAllocatorSetAndGetMaxOrder() {
        NettyConfigProperties.WorkerGroup.Allocator allocator = new NettyConfigProperties.WorkerGroup.Allocator();
        allocator.setMaxOrder(9);
        Assertions.assertEquals(9, allocator.getMaxOrder());
    }

    // ---- RcvBufAllocator ----

    @Test
    void testRcvBufAllocatorDefaults() {
        NettyConfigProperties.WorkerGroup.RcvBufAllocator rcvBufAllocator = new NettyConfigProperties.WorkerGroup.RcvBufAllocator();
        Assertions.assertEquals(64, rcvBufAllocator.getMinimum());
        Assertions.assertEquals(8192, rcvBufAllocator.getInitial());
        Assertions.assertEquals(65535, rcvBufAllocator.getMaximum());
    }

    @Test
    void testRcvBufAllocatorSetAndGetMinimum() {
        NettyConfigProperties.WorkerGroup.RcvBufAllocator rcvBufAllocator = new NettyConfigProperties.WorkerGroup.RcvBufAllocator();
        rcvBufAllocator.setMinimum(128);
        Assertions.assertEquals(128, rcvBufAllocator.getMinimum());
    }

    @Test
    void testRcvBufAllocatorSetAndGetInitial() {
        NettyConfigProperties.WorkerGroup.RcvBufAllocator rcvBufAllocator = new NettyConfigProperties.WorkerGroup.RcvBufAllocator();
        rcvBufAllocator.setInitial(4096);
        Assertions.assertEquals(4096, rcvBufAllocator.getInitial());
    }

    @Test
    void testRcvBufAllocatorSetAndGetMaximum() {
        NettyConfigProperties.WorkerGroup.RcvBufAllocator rcvBufAllocator = new NettyConfigProperties.WorkerGroup.RcvBufAllocator();
        rcvBufAllocator.setMaximum(131070);
        Assertions.assertEquals(131070, rcvBufAllocator.getMaximum());
    }

    // ---- WriteBufferWaterMark ----

    @Test
    void testWriteBufferWaterMarkDefaults() {
        NettyConfigProperties.WorkerGroup.WriteBufferWaterMark mark = new NettyConfigProperties.WorkerGroup.WriteBufferWaterMark();
        Assertions.assertEquals(8 * 1024 * 1024, mark.getLow());
        Assertions.assertEquals(16 * 1024 * 1024, mark.getHigh());
    }

    @Test
    void testWriteBufferWaterMarkSetAndGetLow() {
        NettyConfigProperties.WorkerGroup.WriteBufferWaterMark mark = new NettyConfigProperties.WorkerGroup.WriteBufferWaterMark();
        mark.setLow(4 * 1024 * 1024);
        Assertions.assertEquals(4 * 1024 * 1024, mark.getLow());
    }

    @Test
    void testWriteBufferWaterMarkSetAndGetHigh() {
        NettyConfigProperties.WorkerGroup.WriteBufferWaterMark mark = new NettyConfigProperties.WorkerGroup.WriteBufferWaterMark();
        mark.setHigh(32 * 1024 * 1024);
        Assertions.assertEquals(32 * 1024 * 1024, mark.getHigh());
    }

}
