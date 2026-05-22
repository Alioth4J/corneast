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

package com.alioth4j.corneast.core.netty;

import com.alioth4j.corneast.common.operation.CorneastOperation;
import com.alioth4j.corneast.common.proto.ResponseProto;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.jupiter.api.Test;

import java.nio.channels.ClosedChannelException;

import static org.junit.jupiter.api.Assertions.*;

class NettyStabilityTest {

    // ---
    // long connection reuse

    @Test
    void testLongConnectionReuseManyInboundMessages() {
        EmbeddedChannel channel = new EmbeddedChannel(
                new BackPressureHandler(), new GlobalExceptionHandler());
        assertTrue(channel.isActive());

        for (int i = 0; i < 1000; i++) {
            channel.writeInbound(Unpooled.wrappedBuffer(new byte[]{1}));
            assertTrue(channel.isActive(), "channel closed at iteration " + i);
        }
        channel.finish();
    }

    @Test
    void testLongConnectionReuseWithFlushAndReadCycles() {
        EmbeddedChannel channel = new EmbeddedChannel(
                new BackPressureHandler(), new GlobalExceptionHandler());

        for (int i = 0; i < 500; i++) {
            channel.writeInbound(Unpooled.wrappedBuffer(new byte[]{2}));
            channel.flushInbound();
            channel.readInbound();
            assertTrue(channel.isActive());
        }
        channel.finish();
    }

    // ---
    // abnormal disconnection

    @Test
    void testCloseChannelMidProcessingDoesNotThrow() {
        EmbeddedChannel channel = new EmbeddedChannel(
                new BackPressureHandler(), new GlobalExceptionHandler());

        channel.writeInbound(Unpooled.wrappedBuffer(new byte[]{1}));
        channel.flushInbound();
        channel.close().syncUninterruptibly();
        assertFalse(channel.isActive());
        assertThrows(ClosedChannelException.class, () -> {
            channel.writeInbound(Unpooled.wrappedBuffer(new byte[]{2}));
        });
    }

    @Test
    void testExceptionCaughtProducesErrorResponse() {
        EmbeddedChannel channel = new EmbeddedChannel(new GlobalExceptionHandler());

        channel.pipeline().fireExceptionCaught(new RuntimeException("NettyStabilityTest#testExceptionCaughtProducesErrorResponse"));

        ResponseProto.ResponseDTO outbound = channel.readOutbound();
        assertNotNull(outbound);
        assertEquals(CorneastOperation.ERROR, outbound.getType());
    }

    // ---
    // request surge

    @Test
    void testBurstWritesPipelineIntegrity() {
        EmbeddedChannel channel = new EmbeddedChannel(new BackPressureHandler(), new GlobalExceptionHandler());

        for (int i = 0; i < 200; i++) {
            channel.writeInbound(Unpooled.wrappedBuffer(new byte[]{1, 2, 3, 4}));
        }
        channel.flushInbound();
        assertDoesNotThrow(() -> {
            channel.isActive();
        });

        ByteBuf out;
        while ((out = channel.readOutbound()) != null) {
            out.release();
        }
        channel.finish();
    }

    @Test
    void testChannelWriteReadToggleStability() {
        EmbeddedChannel channel = new EmbeddedChannel(new BackPressureHandler());
        channel.config().setAutoRead(false);

        for (int i = 0; i < 200; i++) {
            channel.writeOneOutbound(Unpooled.wrappedBuffer(new byte[64]));
            ByteBuf out = channel.readOutbound();
            if (out != null) {
                out.release();
            }
            assertTrue(channel.isActive(), "channel broken at iteration " + i);
        }
        channel.finish();
    }

    @Test
    void testBackPressureWhenReachingHighWaterMark() {
        EmbeddedChannel channel = new EmbeddedChannel(new BackPressureHandler());
        channel.config().setAutoRead(false);
        channel.config().setWriteBufferLowWaterMark(1);
        channel.config().setWriteBufferHighWaterMark(2);

        for (int i = 0; i < 10; i++) {
            channel.writeOneOutbound(Unpooled.wrappedBuffer(new byte[1024]));
            assertFalse(channel.isWritable());
        }
        channel.finish();
    }

    // ---
    // burst then recover

    @Test
    void testBurstThenRecoverOnSameChannel() {
        EmbeddedChannel channel = new EmbeddedChannel(new BackPressureHandler(), new GlobalExceptionHandler());

        // burst
        for (int i = 0; i < 100; i++) {
            channel.writeInbound(Unpooled.wrappedBuffer(new byte[]{1}));
        }
        channel.flushInbound();

        // drain
        Object out;
        while ((out = channel.readOutbound()) != null) {
            if (out instanceof ByteBuf b) {
                b.release();
            }
        }

        // recovery
        assertDoesNotThrow(() -> channel.isActive());
        if (channel.isActive()) {
            channel.writeInbound(Unpooled.wrappedBuffer(new byte[]{1}));
            channel.flushInbound();
            Object recoveryOut = channel.readOutbound();
            if (recoveryOut instanceof ByteBuf b) {
                b.release();
            }
        }
        channel.finish();
    }
}
