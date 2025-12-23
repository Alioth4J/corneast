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

package com.alioth4j.corneast_client.send;

import com.alioth4j.corneast_client.config.CorneastConfig;
import com.alioth4j.corneast_client.serialize.AioDeserializer;
import com.alioth4j.corneast_client.serialize.ProtobufSerializer;
import com.alioth4j.corneast_common.proto.RequestProto;
import com.alioth4j.corneast_common.proto.ResponseProto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CompletableFuture;

/**
 * AIO client for sending requests and receiving responses.
 *
 * @author Alioth Null
 */
public class CorneastAioClient {

    private static final Logger log = LoggerFactory.getLogger(CorneastAioClient.class);

    private final String host;
    private final int port;

    private CorneastAioClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public static CorneastAioClient of(CorneastConfig config) {
        if (!config.validate()) {
            throw new IllegalArgumentException("CorneastConfig has not been completely set, current config: " + config);
        }
        return new CorneastAioClient(config.getHost(), config.getPort());
    }

    public CompletableFuture<ResponseProto.ResponseDTO> send(RequestProto.RequestDTO requestDTO) throws IOException {
        byte[] requestBinary = ProtobufSerializer.getInstance().serialize(requestDTO);
        ByteBuffer writeBuf = ByteBuffer.wrap(requestBinary);

        CompletableFuture<ResponseProto.ResponseDTO> outerResponse = new CompletableFuture<>();
        try {
            AsynchronousSocketChannel channel = AsynchronousSocketChannel.open();
            CompletionHandler<Integer, Void> writeHandler = new CompletionHandler<>() {
                @Override
                public void completed(Integer integer, Void unused) {
                    // write
                    if (writeBuf.hasRemaining()) {
                        channel.write(writeBuf, null, this);
                        return;
                    }

                    // read
                    CompletableFuture<ResponseProto.ResponseDTO> innerResponse = AioDeserializer.getInstance().deserialize(channel);
                    innerResponse.whenComplete((responseDTO, throwable) -> {
                        if (throwable != null) {
                            closeQuietly(channel);
                            outerResponse.completeExceptionally(throwable);
                        } else {
                            outerResponse.complete(responseDTO);
                        }
                    });
                }

                @Override
                public void failed(Throwable t, Void unused) {
                    closeQuietly(channel);
                    outerResponse.completeExceptionally(t);
                }
            };
            channel.connect(new InetSocketAddress(host, port), null, new CompletionHandler<Void, Void>() {
                @Override
                public void completed(Void unusedRes, Void unusedAtt) {
                    // write
                    channel.write(writeBuf, null, writeHandler);
                }

                @Override
                public void failed(Throwable t, Void unusedAtt) {
                    closeQuietly(channel);
                    outerResponse.completeExceptionally(t);
                }
            });
        } catch (IOException e) {
            log.error("Error occurs during sending or receiving", e);
            outerResponse.completeExceptionally(e);
        }
        return outerResponse;
    }

    private void closeQuietly(AsynchronousSocketChannel channel) {
        try {
            channel.close();
        } catch (IOException igonred) {
        }
    }

}
