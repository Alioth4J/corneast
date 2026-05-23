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
import com.alioth4j.corneast.common.proto.RequestProto;
import com.alioth4j.corneast.common.proto.ResponseProto;
import com.alioth4j.corneast.core.strategy.RequestHandlingStrategy;
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
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@ExtendWith(MockitoExtension.class)
class RequestRouteHandlerTests {

    private RequestRouteHandler handler = new RequestRouteHandler();

    @Mock
    private ChannelHandlerContext ctx;

    @Mock
    private Map<String, RequestHandlingStrategy> requestHandlingStrategyMap;

    @Mock
    private RequestHandlingStrategy strategy;

    @BeforeEach
    void setup() throws Exception {
        Field field = handler.getClass().getDeclaredField("requestHandlingStrategyMap");
        field.setAccessible(true);
        field.set(handler, requestHandlingStrategyMap);
    }

    @Test
    void testRouteToKnownStrategy() throws Exception {
        RequestProto.RequestDTO requestDTO = RequestProto.RequestDTO.newBuilder()
                .setType(CorneastOperation.REGISTER)
                .setId("req-1")
                .setRegisterReqDTO(RequestProto.RegisterReqDTO.newBuilder()
                        .setKey("key1")
                        .setTokenCount(10)
                        .build())
                .build();
        ResponseProto.ResponseDTO responseDTO = ResponseProto.ResponseDTO.newBuilder()
                .setType(CorneastOperation.REGISTER)
                .setId("req-1")
                .setRegisterRespDTO(ResponseProto.RegisterRespDTO.newBuilder()
                        .setKey("key1")
                        .setSuccess(true)
                        .build())
                .build();

        Mockito.when(requestHandlingStrategyMap.get(CorneastOperation.REGISTER))
               .thenReturn(strategy);
        Mockito.when(strategy.handle(requestDTO))
               .thenReturn(CompletableFuture.completedFuture(responseDTO));

        handler.channelRead0(ctx, requestDTO);

        Mockito.verify(strategy, Mockito.times(1)).handle(requestDTO);
        ArgumentCaptor<ResponseProto.ResponseDTO> captor = ArgumentCaptor.forClass(ResponseProto.ResponseDTO.class);
        Mockito.verify(ctx, Mockito.times(1)).writeAndFlush(captor.capture());
        ResponseProto.ResponseDTO captured = captor.getValue();
        Assertions.assertEquals(CorneastOperation.REGISTER, captured.getType());
        Assertions.assertEquals("req-1", captured.getId());
        Assertions.assertTrue(captured.getRegisterRespDTO().getSuccess());
    }

    @Test
    void testErrorResponsePreservesRequestId() throws Exception {
        RequestProto.RequestDTO requestDTO = RequestProto.RequestDTO.newBuilder()
                .setType(CorneastOperation.REDUCE)
                .setId("my-request-id")
                .setReduceReqDTO(RequestProto.ReduceReqDTO.newBuilder()
                        .setKey("key2")
                        .build())
                .build();

        Mockito.when(requestHandlingStrategyMap.get(CorneastOperation.REDUCE))
               .thenReturn(strategy);
        Mockito.when(strategy.handle(requestDTO))
               .thenReturn(CompletableFuture.failedFuture(new RuntimeException("error")));

        handler.channelRead0(ctx, requestDTO);

        ArgumentCaptor<ResponseProto.ResponseDTO> captor = ArgumentCaptor.forClass(ResponseProto.ResponseDTO.class);
        Mockito.verify(ctx, Mockito.times(1)).writeAndFlush(captor.capture());
        Assertions.assertEquals("my-request-id", captor.getValue().getId());
    }

    @Test
    void testUnknownRequestType() throws Exception {
        RequestProto.RequestDTO requestDTO = RequestProto.RequestDTO.newBuilder()
                .setType("nonexistent")
                .setId("req-unknown")
                .build();

        Mockito.when(requestHandlingStrategyMap.get("nonexistent")).thenReturn(null);

        handler.channelRead0(ctx, requestDTO);

        ArgumentCaptor<ResponseProto.ResponseDTO> captor = ArgumentCaptor.forClass(ResponseProto.ResponseDTO.class);
        Mockito.verify(ctx, Mockito.times(1)).writeAndFlush(captor.capture());
        ResponseProto.ResponseDTO captured = captor.getValue();
        Assertions.assertEquals(CorneastOperation.UNKNOWN, captured.getType());
        Assertions.assertEquals("req-unknown", captured.getId());
    }

    @Test
    void testUnknownRequestTypeDoesNotInvokeStrategy() throws Exception {
        RequestProto.RequestDTO requestDTO = RequestProto.RequestDTO.newBuilder()
                .setType("nonexistent")
                .setId("req-unknown")
                .build();

        Mockito.when(requestHandlingStrategyMap.get("nonexistent")).thenReturn(null);

        handler.channelRead0(ctx, requestDTO);

        Mockito.verify(strategy, Mockito.never()).handle(Mockito.any());
    }

    @Test
    void testStrategyExceptionForRegister() throws Exception {
        RequestProto.RequestDTO requestDTO = RequestProto.RequestDTO.newBuilder()
                .setType(CorneastOperation.REGISTER)
                .setId("req-err-reg")
                .setRegisterReqDTO(RequestProto.RegisterReqDTO.newBuilder()
                        .setKey("reg-key")
                        .setTokenCount(5)
                        .build())
                .build();

        Mockito.when(requestHandlingStrategyMap.get(CorneastOperation.REGISTER))
               .thenReturn(strategy);
        Mockito.when(strategy.handle(requestDTO))
               .thenReturn(CompletableFuture.failedFuture(new RuntimeException("register failed")));

        handler.channelRead0(ctx, requestDTO);

        ArgumentCaptor<ResponseProto.ResponseDTO> captor = ArgumentCaptor.forClass(ResponseProto.ResponseDTO.class);
        Mockito.verify(ctx, Mockito.times(1)).writeAndFlush(captor.capture());
        ResponseProto.ResponseDTO captured = captor.getValue();
        Assertions.assertEquals(CorneastOperation.REGISTER, captured.getType());
        Assertions.assertEquals("req-err-reg", captured.getId());
        Assertions.assertNotNull(captured.getRegisterRespDTO());
        Assertions.assertFalse(captured.getRegisterRespDTO().getSuccess());
        Assertions.assertEquals("reg-key", captured.getRegisterRespDTO().getKey());
    }

    @Test
    void testStrategyExceptionForReduce() throws Exception {
        RequestProto.RequestDTO requestDTO = RequestProto.RequestDTO.newBuilder()
                .setType(CorneastOperation.REDUCE)
                .setId("req-err-reduce")
                .setReduceReqDTO(RequestProto.ReduceReqDTO.newBuilder()
                        .setKey("reduce-key")
                        .build())
                .build();

        Mockito.when(requestHandlingStrategyMap.get(CorneastOperation.REDUCE))
               .thenReturn(strategy);
        Mockito.when(strategy.handle(requestDTO))
               .thenReturn(CompletableFuture.failedFuture(new RuntimeException("reduce failed")));

        handler.channelRead0(ctx, requestDTO);

        ArgumentCaptor<ResponseProto.ResponseDTO> captor = ArgumentCaptor.forClass(ResponseProto.ResponseDTO.class);
        Mockito.verify(ctx, Mockito.times(1)).writeAndFlush(captor.capture());
        ResponseProto.ResponseDTO captured = captor.getValue();
        Assertions.assertEquals(CorneastOperation.REDUCE, captured.getType());
        Assertions.assertEquals("req-err-reduce", captured.getId());
        Assertions.assertNotNull(captured.getReduceRespDTO());
        Assertions.assertFalse(captured.getReduceRespDTO().getSuccess());
        Assertions.assertEquals("reduce-key", captured.getReduceRespDTO().getKey());
    }

    @Test
    void testStrategyExceptionForRelease() throws Exception {
        RequestProto.RequestDTO requestDTO = RequestProto.RequestDTO.newBuilder()
                .setType(CorneastOperation.RELEASE)
                .setId("req-err-release")
                .setReleaseReqDTO(RequestProto.ReleaseReqDTO.newBuilder()
                        .setKey("release-key")
                        .build())
                .build();

        Mockito.when(requestHandlingStrategyMap.get(CorneastOperation.RELEASE))
               .thenReturn(strategy);
        Mockito.when(strategy.handle(requestDTO))
               .thenReturn(CompletableFuture.failedFuture(new RuntimeException("release failed")));

        handler.channelRead0(ctx, requestDTO);

        ArgumentCaptor<ResponseProto.ResponseDTO> captor = ArgumentCaptor.forClass(ResponseProto.ResponseDTO.class);
        Mockito.verify(ctx, Mockito.times(1)).writeAndFlush(captor.capture());
        ResponseProto.ResponseDTO captured = captor.getValue();
        Assertions.assertEquals(CorneastOperation.RELEASE, captured.getType());
        Assertions.assertEquals("req-err-release", captured.getId());
        Assertions.assertNotNull(captured.getReleaseRespDTO());
        Assertions.assertFalse(captured.getReleaseRespDTO().getSuccess());
        Assertions.assertEquals("release-key", captured.getReleaseRespDTO().getKey());
    }

    @Test
    void testStrategyExceptionForQuery() throws Exception {
        RequestProto.RequestDTO requestDTO = RequestProto.RequestDTO.newBuilder()
                .setType(CorneastOperation.QUERY)
                .setId("req-err-query")
                .setQueryReqDTO(RequestProto.QueryReqDTO.newBuilder()
                        .setKey("query-key")
                        .build())
                .build();

        Mockito.when(requestHandlingStrategyMap.get(CorneastOperation.QUERY))
               .thenReturn(strategy);
        Mockito.when(strategy.handle(requestDTO))
               .thenReturn(CompletableFuture.failedFuture(new RuntimeException("query failed")));

        handler.channelRead0(ctx, requestDTO);

        ArgumentCaptor<ResponseProto.ResponseDTO> captor = ArgumentCaptor.forClass(ResponseProto.ResponseDTO.class);
        Mockito.verify(ctx, Mockito.times(1)).writeAndFlush(captor.capture());
        ResponseProto.ResponseDTO captured = captor.getValue();
        Assertions.assertEquals(CorneastOperation.QUERY, captured.getType());
        Assertions.assertEquals("req-err-query", captured.getId());
        Assertions.assertNotNull(captured.getQueryRespDTO());
        Assertions.assertEquals(-1L, captured.getQueryRespDTO().getRemainingTokenCount());
        Assertions.assertEquals("query-key", captured.getQueryRespDTO().getKey());
    }

    @Test
    void testFlyweightBuildersProducesCorrectResponseForSequentialCalls() throws Exception {
        RequestProto.RequestDTO request1 = RequestProto.RequestDTO.newBuilder()
                .setType(CorneastOperation.REGISTER)
                .setId("req-1")
                .setRegisterReqDTO(RequestProto.RegisterReqDTO.newBuilder()
                        .setKey("key-alpha")
                        .setTokenCount(1)
                        .build())
                .build();

        RequestProto.RequestDTO request2 = RequestProto.RequestDTO.newBuilder()
                .setType(CorneastOperation.REGISTER)
                .setId("req-2")
                .setRegisterReqDTO(RequestProto.RegisterReqDTO.newBuilder()
                        .setKey("key-beta")
                        .setTokenCount(2)
                        .build())
                .build();

        Mockito.when(requestHandlingStrategyMap.get(CorneastOperation.REGISTER))
               .thenReturn(strategy);
        Mockito.when(strategy.handle(Mockito.any()))
               .thenReturn(CompletableFuture.failedFuture(new RuntimeException("error")));

        handler.channelRead0(ctx, request1);

        ArgumentCaptor<ResponseProto.ResponseDTO> captor1 = ArgumentCaptor.forClass(ResponseProto.ResponseDTO.class);
        Mockito.verify(ctx, Mockito.times(1)).writeAndFlush(captor1.capture());
        Assertions.assertEquals("req-1", captor1.getValue().getId());
        Assertions.assertEquals("key-alpha", captor1.getValue().getRegisterRespDTO().getKey());

        handler.channelRead0(ctx, request2);

        ArgumentCaptor<ResponseProto.ResponseDTO> captor2 = ArgumentCaptor.forClass(ResponseProto.ResponseDTO.class);
        Mockito.verify(ctx, Mockito.times(2)).writeAndFlush(captor2.capture());
        Assertions.assertEquals("req-2", captor2.getValue().getId());
        Assertions.assertEquals("key-beta", captor2.getValue().getRegisterRespDTO().getKey());
    }

}
