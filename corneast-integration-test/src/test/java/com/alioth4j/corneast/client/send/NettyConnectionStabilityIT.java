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

package com.alioth4j.corneast.client.send;

import com.alioth4j.corneast.client.config.CorneastConfig;
import com.alioth4j.corneast.client.eureka.EurekaConsumer;
import com.alioth4j.corneast.client.request.CorneastRequest;
import com.alioth4j.corneast.common.algo.RandomSelector;
import com.alioth4j.corneast.common.algo.Selector;
import com.alioth4j.corneast.common.operation.CorneastOperation;
import com.alioth4j.corneast.common.proto.RequestProto;
import com.alioth4j.corneast.common.proto.ResponseProto;
import com.netflix.appinfo.InstanceInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.Socket;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

public class NettyConnectionStabilityIT {

    private static final int LONG_REUSE_COUNT = 10;

    private static CorneastConfig discoverConfig() {
        EurekaConsumer consumer = new EurekaConsumer();
        List<InstanceInfo> instances = consumer.getInstanceInfos();
        Selector<InstanceInfo> selector = new RandomSelector<>(instances);
        InstanceInfo instance = selector.select();
        CorneastConfig config = new CorneastConfig();
        config.setHost(instance.getHostName());
        config.setPort(instance.getPort());
        return config;
    }


    // ---
    // long connection reuse

    @Test
    void testLongConnectionReuseWithBioClient() {
        String key = "NettyConnectionStabilityIT#testLongConnectionReuseBio";
        RequestProto.RequestDTO registerReq = new CorneastRequest(CorneastOperation.REGISTER, "", key, 2 * LONG_REUSE_COUNT).instance;
        RequestProto.RequestDTO reduceReq = new CorneastRequest(CorneastOperation.REDUCE, "", key).instance;

        CorneastConfig config = discoverConfig();

        try (CorneastBioClient client = CorneastBioClient.of(config)) {
            ResponseProto.ResponseDTO regResp = client.send(registerReq);
            Assertions.assertTrue(regResp.getRegisterRespDTO().getSuccess());

            Socket socket1 = null;
            try {
                Field socketField1 = client.getClass().getDeclaredField("socket");
                socketField1.setAccessible(true);
                socket1 = (Socket) socketField1.get(client);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            for (int i = 0; i < LONG_REUSE_COUNT; i++) {
                ResponseProto.ResponseDTO resp = client.send(reduceReq);
                Assertions.assertEquals(CorneastOperation.REDUCE, resp.getType());
                Assertions.assertTrue(resp.getReduceRespDTO().getSuccess(),
                        "failure at iteration " + i);
                Socket socket2 = null;
                try {
                    Field socketField2 = client.getClass().getDeclaredField("socket");
                    socketField2.setAccessible(true);
                    socket2 = (Socket) socketField2.get(client);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                Assertions.assertSame(socket1, socket2);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testLongConnectionReuseWithNioClient() {
        String key = "NettyConnectionStabilityIT#testLongConnectionReuseNio";
        RequestProto.RequestDTO registerReq = new CorneastRequest(CorneastOperation.REGISTER, "", key, 2 * LONG_REUSE_COUNT).instance;
        RequestProto.RequestDTO reduceReq = new CorneastRequest(CorneastOperation.REDUCE, "", key).instance;

        CorneastConfig config = discoverConfig();

        try (CorneastNioClient client = CorneastNioClient.of(config)) {
            ResponseProto.ResponseDTO regResp = client.send(registerReq);
            Assertions.assertTrue(regResp.getRegisterRespDTO().getSuccess());

            SocketChannel socketChannel1 = null;
            try {
                Field socketChannelField1 = client.getClass().getDeclaredField("socketChannel");
                socketChannelField1.setAccessible(true);
                socketChannel1 = (SocketChannel) socketChannelField1.get(client);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            for (int i = 0; i < LONG_REUSE_COUNT; i++) {
                ResponseProto.ResponseDTO resp = client.send(reduceReq);
                Assertions.assertEquals(CorneastOperation.REDUCE, resp.getType());
                Assertions.assertTrue(resp.getReduceRespDTO().getSuccess(),
                        "failure at iteration " + i);

                SocketChannel socketChannel2 = null;
                try {
                    Field socketChannelField2 = client.getClass().getDeclaredField("socketChannel");
                    socketChannelField2.setAccessible(true);
                    socketChannel2 = (SocketChannel) socketChannelField2.get(client);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                Assertions.assertSame(socketChannel1, socketChannel2);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testLongConnectionReuseWithAioClient() {
        String key = "NettyConnectionStabilityIT#testLongConnectionReuseAio";
        RequestProto.RequestDTO registerReq = new CorneastRequest(CorneastOperation.REGISTER, "", key, 2 * LONG_REUSE_COUNT).instance;
        RequestProto.RequestDTO reduceReq = new CorneastRequest(CorneastOperation.REDUCE, "", key).instance;

        CorneastConfig config = discoverConfig();

        try (CorneastAioClient client = CorneastAioClient.of(config)) {
            CompletableFuture<ResponseProto.ResponseDTO> regRespFuture = client.send(registerReq);
            regRespFuture.whenComplete((responseDTO, t) -> {
                Assertions.assertNull(t);
                Assertions.assertTrue(responseDTO.getRegisterRespDTO().getSuccess());
            });

            AsynchronousSocketChannel aSocketChannel1 = null;
            try {
                Field aSocketChannelField1 = client.getClass().getDeclaredField("aSocketChannel");
                aSocketChannelField1.setAccessible(true);
                aSocketChannel1 = (AsynchronousSocketChannel) aSocketChannelField1.get(client);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            final AsynchronousSocketChannel aSocketChannel1Final = aSocketChannel1;
            for (int i = 0; i < LONG_REUSE_COUNT; i++) {
                final int idx = i;
                CompletableFuture<ResponseProto.ResponseDTO> responseFuture = client.send(reduceReq);
                responseFuture.whenComplete((responseDTO, t) -> {
                    Assertions.assertEquals(CorneastOperation.REDUCE, responseDTO.getType());
                    Assertions.assertTrue(responseDTO.getReduceRespDTO().getSuccess(),
                            "failure at iteration " + idx);

                    AsynchronousSocketChannel aSocketChannel2 = null;
                    try {
                        Field aSocketChannelField2 = client.getClass().getDeclaredField("aSocketChannel");
                        aSocketChannelField2.setAccessible(true);
                        aSocketChannel2 = (AsynchronousSocketChannel) aSocketChannelField2.get(client);
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                    Assertions.assertSame(aSocketChannel1Final, aSocketChannel2);
                });
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // ---
    // reconnect

    @Test
    void testDisconnectionAndReconnect() {
        String key = "NettyConnectionStabilityIT#testDisconnectAndReconnect";
        RequestProto.RequestDTO registerReq = new CorneastRequest(CorneastOperation.REGISTER, "", key, 4 * LONG_REUSE_COUNT).instance;
        RequestProto.RequestDTO reduceReq = new CorneastRequest(CorneastOperation.REDUCE, "", key).instance;

        CorneastConfig config = discoverConfig();

        // first connect
        try (CorneastBioClient client = CorneastBioClient.of(config)) {
            client.send(registerReq);
            for (int i = 0; i < LONG_REUSE_COUNT; i++) {
                ResponseProto.ResponseDTO resp = client.send(reduceReq);
                Assertions.assertTrue(resp.getReduceRespDTO().getSuccess(), "first connection failure at iteration " + i);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // disconnect and reconnect

        // second connect
        try (CorneastBioClient client2 = CorneastBioClient.of(config)) {
            for (int i = 0; i < LONG_REUSE_COUNT; i++) {
                ResponseProto.ResponseDTO resp = client2.send(reduceReq);
                Assertions.assertTrue(resp.getReduceRespDTO().getSuccess(), "second connection failure at iteration " + i);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testMultipleReconnectCycles() {
        String key = "NettyConnectionStabilityIT#testMultipleReconnectCycles";
        RequestProto.RequestDTO registerReq = new CorneastRequest(CorneastOperation.REGISTER, "", key, 20 * LONG_REUSE_COUNT).instance;
        RequestProto.RequestDTO releaseReq = new CorneastRequest(CorneastOperation.REDUCE, "", key).instance;

        CorneastConfig config = discoverConfig();

        for (int cycle = 0; cycle < 10; cycle++) {
            try (CorneastNioClient client = CorneastNioClient.of(config)) {
                if (cycle == 0) {
                    client.send(registerReq);
                }
                for (int i = 0; i < LONG_REUSE_COUNT; i++) {
                    ResponseProto.ResponseDTO resp = client.send(releaseReq);
                    Assertions.assertEquals(CorneastOperation.REDUCE, resp.getType());
                    Assertions.assertTrue(resp.getReduceRespDTO().getSuccess(), "cycle " + cycle + " failure at iteration " + i);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // ---
    // request surge

    @Test
    void testRequestSurge() {
        String key = "NettyConnectionStabilityIT#testRequestSurge";
        RequestProto.RequestDTO registerReq = new CorneastRequest(CorneastOperation.REGISTER, "", key, 1000).instance;
        RequestProto.RequestDTO reduceReq = new CorneastRequest(CorneastOperation.REDUCE, "", key).instance;

        CorneastConfig config = discoverConfig();

        try (CorneastNioClient client = CorneastNioClient.of(config)) {
            client.send(registerReq);
            for (int i = 0; i < 500; i++) {
                ResponseProto.ResponseDTO resp = client.send(reduceReq);
                Assertions.assertTrue(resp.getReduceRespDTO().getSuccess(), "surge failure at iteration " + i);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testMixedOperationSurge() {
        String key = "NettyConnectionStabilityIT#testMixedOperationSurge";
        List<RequestProto.RequestDTO> requestList = List.of(new CorneastRequest(CorneastOperation.REGISTER, "", key, 1000).instance,
                                                            new CorneastRequest(CorneastOperation.REDUCE, "", key).instance,
                                                            new CorneastRequest(CorneastOperation.RELEASE, "", key).instance,
                                                            new CorneastRequest(CorneastOperation.QUERY, "", key).instance);
        CorneastConfig config = discoverConfig();
        RandomSelector<RequestProto.RequestDTO> selector = new RandomSelector<>(requestList);

        try (CorneastNioClient client = CorneastNioClient.of(config)) {
            for (int i = 0; i < 1000; i++) {
                Assertions.assertDoesNotThrow(() -> client.send(selector.select()));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
