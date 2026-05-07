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

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BackPressureHandlerTests {

    private BackPressureHandler handler = new BackPressureHandler();

    @Mock
    private ChannelHandlerContext ctx;

    @Mock
    private Channel channel;

    @BeforeEach
    void setup() {
        Mockito.when(ctx.channel()).thenReturn(channel);
    }

    @Test
    void testChannelActiveWhenWritable() throws Exception {
        Mockito.when(channel.isWritable()).thenReturn(true);
        handler.channelActive(ctx);
        Mockito.verify(ctx, Mockito.times(1)).read();
        Mockito.verify(ctx, Mockito.times(1)).fireChannelActive();
    }

    @Test
    void testChannelActiveWhenUnwritable() throws Exception {
        Mockito.when(channel.isWritable()).thenReturn(false);
        handler.channelActive(ctx);
        Mockito.verify(ctx, Mockito.never()).read();
        Mockito.verify(ctx, Mockito.times(1)).fireChannelActive();
    }

    @Test
    void testChannelReadCompleteWhenActiveAndWritable() throws Exception {
        Mockito.when(channel.isActive()).thenReturn(true);
        Mockito.when(channel.isWritable()).thenReturn(true);
        handler.channelReadComplete(ctx);
        Mockito.verify(ctx, Mockito.times(1)).read();
        Mockito.verify(ctx, Mockito.times(1)).fireChannelReadComplete();
    }

    @Test
    void testChannelReadCompleteWhenActiveAndUnwritable() throws Exception {
        Mockito.when(channel.isActive()).thenReturn(true);
        Mockito.when(channel.isWritable()).thenReturn(false);
        handler.channelReadComplete(ctx);
        Mockito.verify(ctx, Mockito.never()).read();
        Mockito.verify(ctx, Mockito.times(1)).fireChannelReadComplete();
    }

    @Test
    void testChannelReadCompleteWhenInactive() throws Exception {
        Mockito.when(channel.isActive()).thenReturn(false);
        handler.channelReadComplete(ctx);
        Mockito.verify(ctx, Mockito.never()).read();
        Mockito.verify(ctx, Mockito.times(1)).fireChannelReadComplete();
    }

    @Test
    void testChannelWritabilityChangedWhenWritable() throws Exception {
        Mockito.when(channel.isWritable()).thenReturn(true);
        handler.channelWritabilityChanged(ctx);
        Mockito.verify(ctx, Mockito.times(1)).read();
        Mockito.verify(ctx, Mockito.times(1)).fireChannelWritabilityChanged();
    }

    @Test
    void testChannelWritabilityChangedWhenUnwritable() throws Exception {
        Mockito.when(channel.isWritable()).thenReturn(false);
        handler.channelWritabilityChanged(ctx);
        Mockito.verify(ctx, Mockito.never()).read();
        Mockito.verify(ctx, Mockito.times(1)).fireChannelWritabilityChanged();
    }

}
