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
import com.esotericsoftware.kryo.serializers.TaggedFieldSerializer;
import io.netty.channel.ChannelHandlerContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTests {

    @Mock
    private ChannelHandlerContext ctx;

    @Test
    void testExceptionCaughtWritesErrorResponse() throws Exception {
        GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();
        RuntimeException cause = new RuntimeException("test RuntimeException in testRuntimeException()");
        globalExceptionHandler.exceptionCaught(ctx, cause);
        ArgumentCaptor<ResponseProto.ResponseDTO> argument = ArgumentCaptor.forClass(ResponseProto.ResponseDTO.class);
        Mockito.verify(ctx, Mockito.times(1)).writeAndFlush(argument.capture());
        Assertions.assertEquals(CorneastOperation.ERROR, argument.getValue().getType());
    }

    @Test
    void testExceptionCaughtClosesContext() throws Exception {
        GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();
        RuntimeException cause = new RuntimeException("test RuntimeException in testExceptionCaughtClosesContext()");
        globalExceptionHandler.exceptionCaught(ctx, cause);
        Mockito.verify(ctx, Mockito.times(1)).close();
    }

    @Test
    void testExceptionCaughtWriteAndFlushBeforeClose() throws Exception {
        GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();
        RuntimeException cause = new RuntimeException("test RuntimeException in testExceptionCaughtWriteAndFlushBeforeClose()");
        globalExceptionHandler.exceptionCaught(ctx, cause);
        InOrder inOrder = Mockito.inOrder(ctx);
        inOrder.verify(ctx).writeAndFlush(Mockito.any());
        inOrder.verify(ctx).close();
    }

}
