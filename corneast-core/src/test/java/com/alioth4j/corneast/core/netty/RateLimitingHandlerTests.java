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
import com.google.common.util.concurrent.RateLimiter;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;


@ExtendWith(MockitoExtension.class)
class RateLimitingHandlerTests {

    private RateLimitingHandler handler = new RateLimitingHandler();

    @Mock
    private RateLimiter rateLimiter;

    @Mock
    private ChannelHandlerContext ctx;

    @BeforeEach
    void setup() throws Exception {
        Field rateLimiterField = handler.getClass().getDeclaredField("rateLimiter");
        rateLimiterField.setAccessible(true);
        rateLimiterField.set(handler, rateLimiter);
    }

    @Test
    void testChannelRead0Pass() throws Exception {
        Mockito.when(rateLimiter.tryAcquire()).thenReturn(true);
        handler.channelRead0(ctx, Unpooled.buffer());
        Mockito.verify(ctx, Mockito.times(1)).fireChannelRead(Mockito.any(ByteBuf.class));
        Mockito.verify(ctx, Mockito.never()).writeAndFlush(Mockito.any(ResponseProto.ResponseDTO.class));
        Mockito.verify(ctx, Mockito.never()).close();
    }

    @Test
    void testChannelRead0Block() throws Exception {
        Mockito.when(rateLimiter.tryAcquire()).thenReturn(false);
        handler.channelRead0(ctx, null);
        Mockito.verify(ctx, Mockito.never()).fireChannelRead(Mockito.any(ByteBuf.class));
        Mockito.verify(ctx, Mockito.times(1)).writeAndFlush(Mockito.any(ResponseProto.ResponseDTO.class));
        Mockito.verify(ctx, Mockito.times(1)).close();
    }

    @Test
    void testChannelRead0BlockResponseType() throws Exception {
        Mockito.when(rateLimiter.tryAcquire()).thenReturn(false);
        handler.channelRead0(ctx, null);
        ArgumentCaptor<ResponseProto.ResponseDTO> argument = ArgumentCaptor.forClass(ResponseProto.ResponseDTO.class);
        Mockito.verify(ctx, Mockito.times(1)).writeAndFlush(argument.capture());
        Assertions.assertEquals(CorneastOperation.RATE_LIMITED, argument.getValue().getType());
    }

}
