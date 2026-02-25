/*
 * Corneast
 * Copyright (C) 2025-2026 Alioth Null
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
import com.alioth4j.corneast.client.serialize.AioDeserializer;
import com.alioth4j.corneast.client.serialize.ProtobufSerializer;
import com.alioth4j.corneast.common.proto.RequestProto;
import com.alioth4j.corneast.common.proto.ResponseProto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * AIO client for sending requests and receiving responses.
 *
 * @author Alioth Null
 */
public class CorneastAioClient implements Closeable {

    private static final Logger log = LoggerFactory.getLogger(CorneastAioClient.class);

    private final String host;
    private final int port;

    private volatile boolean closed = false;
    private volatile boolean connected = false;

    private AsynchronousSocketChannel aSocketChannel;
    private CompletableFuture<Void> chainTail = CompletableFuture.completedFuture(null);

    private final Object chainLock = new Object();
    private final Object lifecycleLock = new Object();

    private CorneastAioClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public static CorneastAioClient of(CorneastConfig config) throws IOException {
        if (!config.validate()) {
            throw new IllegalArgumentException("CorneastConfig has not been completely set, current config: " + config);
        }
        CorneastAioClient instance = new CorneastAioClient(config.getHost(), config.getPort());
        instance.ensureConnected();
        return instance;
    }

    public CompletableFuture<ResponseProto.ResponseDTO> send(RequestProto.RequestDTO requestDTO) throws IOException {
        byte[] requestBinary = ProtobufSerializer.getInstance().serialize(requestDTO);
        CompletableFuture<ResponseProto.ResponseDTO> responseFuture = new CompletableFuture<>();

        synchronized (chainLock) {
            chainTail = chainTail.thenCompose(ignored -> doSend(requestBinary, responseFuture));
        }
        return responseFuture;
    }

    private CompletableFuture<Void> doSend(byte[] requestBinary, CompletableFuture<ResponseProto.ResponseDTO> responseFuture) {
        CompletableFuture<Void> marker = new CompletableFuture<>();

        AsynchronousSocketChannel connectedASocketChannel;
        try {
            connectedASocketChannel = ensureConnected();
        } catch (IOException e) {
            responseFuture.completeExceptionally(e);
            marker.complete(null);
            handleChannelException(e);
            return marker;
        }

        ByteBuffer writeBuf = ByteBuffer.wrap(requestBinary);
        connectedASocketChannel.write(writeBuf, null, new CompletionHandler<Integer, Void>() {
            @Override
            public void completed(Integer result, Void attachment) {
                if (writeBuf.hasRemaining()) {
                    connectedASocketChannel.write(writeBuf, null, null);
                } else {
                    AioDeserializer.getInstance().deserialize(connectedASocketChannel)
                            .whenComplete((responseDTO, throwable) -> {
                                if (throwable != null) {
                                    responseFuture.completeExceptionally(throwable);
                                    handleChannelException(throwable);
                                } else {
                                    responseFuture.complete(responseDTO);
                                }
                                marker.complete(null);
                            });
                }
            }

            @Override
            public void failed(Throwable t, Void attachment) {
                responseFuture.completeExceptionally(t);
                handleChannelException(t);
                marker.complete(null);
            }
        });

        return marker;
    }

    private AsynchronousSocketChannel ensureConnected() throws IOException {
        synchronized (lifecycleLock) {
            if (closed) {
                throw new IOException("CorneastAioClient has been closed");
            }
            if (aSocketChannel != null && aSocketChannel.isOpen() && connected) {
                return aSocketChannel;
            }
            resetChannel();

            try {
                aSocketChannel = AsynchronousSocketChannel.open();
                aSocketChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
                aSocketChannel.setOption(StandardSocketOptions.TCP_NODELAY, true);
                aSocketChannel.connect(new InetSocketAddress(host, port)).get();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                resetChannel();
                throw new IOException("Interrupted while connecting", e);
            } catch (ExecutionException e) {
                resetChannel();
                Throwable cause = e.getCause() == null ? e : e.getCause();
                throw new IOException("Failed to connect", cause);
            } finally {
                connected = true;
            }
            return aSocketChannel;
        }
    }

    private void handleChannelException(Throwable t) {
        log.warn("AIO fails: {}", t.getMessage());
        log.warn("Resetting AsynchronousSocketChannel...");
        synchronized (lifecycleLock) {
            resetChannel();
        }
    }

    private void resetChannel() {
        AsynchronousSocketChannel current = aSocketChannel;
        aSocketChannel = null;
        connected = false;
        if (current != null) {
            try {
                current.close();
            } catch (IOException ignored) {
            }
        }
    }

    @Override
    public void close() {
        synchronized (lifecycleLock) {
            closed = true;
            resetChannel();
        }
    }

}
