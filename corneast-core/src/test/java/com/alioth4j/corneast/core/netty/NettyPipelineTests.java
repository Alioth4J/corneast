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
import com.alioth4j.corneast.core.netty.spi.NettyCustomHandler;
import com.alioth4j.corneast.core.strategy.RequestHandlingStrategy;
import com.google.common.util.concurrent.RateLimiter;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class NettyPipelineTests {

    @Mock
    private RateLimiter rateLimiter;

    @Mock
    private Map<String, RequestHandlingStrategy> requestHandlingStrategyMap;

    @Mock
    private RequestHandlingStrategy strategy;

    private RateLimitingHandler rateLimitingHandler;
    private RequestRouteHandler requestRouteHandler;

    @BeforeEach
    void setup() throws Exception {
        rateLimitingHandler = new RateLimitingHandler();
        Field rateLimiterField = rateLimitingHandler.getClass().getDeclaredField("rateLimiter");
        rateLimiterField.setAccessible(true);
        rateLimiterField.set(rateLimitingHandler, rateLimiter);

        requestRouteHandler = new RequestRouteHandler();
        Field strategyMapField = requestRouteHandler.getClass().getDeclaredField("requestHandlingStrategyMap");
        strategyMapField.setAccessible(true);
        strategyMapField.set(requestRouteHandler, requestHandlingStrategyMap);
    }

    @ChannelHandler.Sharable
    private static class TestCustomHandler extends ChannelInboundHandlerAdapter implements NettyCustomHandler {

        private final int order;

        TestCustomHandler(int order) {
            this.order = order;
        }

        @Override
        public int getOrder() {
            return order;
        }

    }

    @Test
    void testHandlerOrderInPipeline() {
        EmbeddedChannel channel = new EmbeddedChannel();
        channel.config().setAutoRead(false);

        // Build pipeline in the same order as NettyServer
        channel.pipeline().addLast(new BackPressureHandler());
        channel.pipeline().addLast(rateLimitingHandler);
        channel.pipeline().addLast(new ProtobufVarint32FrameDecoder());
        channel.pipeline().addLast(new ProtobufDecoder(RequestProto.RequestDTO.getDefaultInstance()));
        channel.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
        channel.pipeline().addLast(new ProtobufEncoder());
        channel.pipeline().addLast(new IdempotentHandler());
        channel.pipeline().addLast(requestRouteHandler);
        channel.pipeline().addLast(new GlobalExceptionHandler());

        Map<String, ChannelHandler> handlerMap = channel.pipeline().toMap();
        Object[] handlers = handlerMap.values().toArray();

        int bpIdx = findHandlerIndex(handlers, BackPressureHandler.class);
        int rlIdx = findHandlerIndex(handlers, RateLimitingHandler.class);
        int frameIdx = findHandlerIndex(handlers, ProtobufVarint32FrameDecoder.class);
        int decIdx = findHandlerIndex(handlers, ProtobufDecoder.class);
        int prepIdx = findHandlerIndex(handlers, ProtobufVarint32LengthFieldPrepender.class);
        int encIdx = findHandlerIndex(handlers, ProtobufEncoder.class);
        int idemIdx = findHandlerIndex(handlers, IdempotentHandler.class);
        int routeIdx = findHandlerIndex(handlers, RequestRouteHandler.class);
        int excIdx = findHandlerIndex(handlers, GlobalExceptionHandler.class);

        Assertions.assertTrue(bpIdx < rlIdx, "backPressure < rateLimiting");
        Assertions.assertTrue(rlIdx < frameIdx, "rateLimiting < frameDecoder");
        Assertions.assertTrue(frameIdx < decIdx, "frameDecoder < protobufDecoder");
        Assertions.assertTrue(decIdx < prepIdx, "protobufDecoder < lengthFieldPrepender");
        Assertions.assertTrue(prepIdx < encIdx, "lengthFieldPrepender < protobufEncoder");
        Assertions.assertTrue(encIdx < idemIdx, "protobufEncoder < idempotent");
        Assertions.assertTrue(idemIdx < routeIdx, "idempotent < requestRoute");
        Assertions.assertTrue(routeIdx < excIdx, "requestRoute < globalException");

        channel.finish();
    }

    @Test
    void testMultipleCustomHandlers() {
        EmbeddedChannel channel = new EmbeddedChannel();
        channel.config().setAutoRead(false);

        TestCustomHandler lowOrder = new TestCustomHandler(10);
        TestCustomHandler midOrder = new TestCustomHandler(20);
        TestCustomHandler highOrder = new TestCustomHandler(30);

        channel.pipeline().addLast(new BackPressureHandler());
        channel.pipeline().addLast(new IdempotentHandler());
        channel.pipeline().addLast(lowOrder);
        channel.pipeline().addLast(midOrder);
        channel.pipeline().addLast(highOrder);
        channel.pipeline().addLast(requestRouteHandler);
        channel.pipeline().addLast(new GlobalExceptionHandler());

        Map<String, ChannelHandler> handlerMap = channel.pipeline().toMap();
        Object[] handlers = handlerMap.values().toArray();

        int idempotentIdx = findHandlerIndex(handlers, IdempotentHandler.class);
        int routeIdx = findHandlerIndex(handlers, RequestRouteHandler.class);
        Assertions.assertTrue(idempotentIdx >= 0);
        Assertions.assertTrue(routeIdx > idempotentIdx + 1, "expect custom handlers between idempotent and route");

        for (int i = idempotentIdx + 1; i < routeIdx; i++) {
            Assertions.assertInstanceOf(TestCustomHandler.class, handlers[i]);
        }

        channel.finish();
    }

    @Test
    void testExceptionCaughtProducesErrorResponse() {
        EmbeddedChannel channel = new EmbeddedChannel(new GlobalExceptionHandler());

        channel.pipeline().fireExceptionCaught(new RuntimeException("test exception in pipeline"));

        ResponseProto.ResponseDTO outbound = channel.readOutbound();
        Assertions.assertNotNull(outbound);
        Assertions.assertEquals(CorneastOperation.ERROR, outbound.getType());

        channel.finish();
    }

    private static int findHandlerIndex(Object[] handlers, Class<?> handlerClass) {
        for (int i = 0; i < handlers.length; i++) {
            if (handlerClass.isInstance(handlers[i])) {
                return i;
            }
        }
        throw new RuntimeException(handlerClass.getName() + " does not exist");
    }

}
