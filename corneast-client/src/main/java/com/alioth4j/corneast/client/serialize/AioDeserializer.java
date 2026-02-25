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

package com.alioth4j.corneast.client.serialize;

import com.alioth4j.corneast.common.proto.ResponseProto;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CompletableFuture;

/**
 * Deserializer implemented with AIO.
 * <p>
 * It's a singleton.
 *
 * @author Alioth Null
 */
public final class AioDeserializer extends AbstractDeserializer {

    private static volatile AioDeserializer instance;

    private AioDeserializer() {
    }

    public static AioDeserializer getInstance() {
        if (instance == null) {
            synchronized (AioDeserializer.class) {
                if (instance == null) {
                    instance = new AioDeserializer();
                }
            }
        }
        return instance;
    }

    /**
     * Deserializes for AIO.
     * @param channel AIO channel
     * @return CompletableFuture of response object
     */
    public CompletableFuture<ResponseProto.ResponseDTO> deserialize(AsynchronousSocketChannel channel) {
        CompletableFuture<ResponseProto.ResponseDTO> response = new CompletableFuture<>();
        ByteBuffer oneByte = ByteBuffer.allocate(1);
        // state[0] = result, state[1] = shift
        int[] state = new int[]{0, 0};

        channel.read(oneByte, null, new CompletionHandler<Integer, Void>() {
            @Override
            public void completed(Integer bytesRead, Void att) {
                if (bytesRead == -1) {
                    response.completeExceptionally(new IOException("Stream closed while reading length prefix"));
                    return;
                }
                oneByte.flip();
                int b = oneByte.get() & 0xFF;
                state[0] |= (b & 0x7F) << state[1];
                if ((b & 0x80) != 0) {
                    state[1] += 7;
                    oneByte.clear();
                    channel.read(oneByte, null, this);
                } else {
                    int length = state[0];
                    if (length < 0 || length > MAX_PAYLOAD_SIZE) {
                        response.completeExceptionally(new IOException("Invalid payload length: " + length));
                        return;
                    }
                    readPayload(channel, length, response);
                }
            }

            @Override
            public void failed(Throwable t, Void att) {
                response.completeExceptionally(t);
            }
        });
        return response;
    }

    /**
     * Gets the payload without length prefix.
     * It is a helper method for Aio, invoked by <code>deserialize(AsynchronousSocketChannel, CompletableFuture<ResponseProto.ResponseDTO>)</code>
     * @param channel AIO channel
     * @param length payload length
     * @param response CompletableFuture of response object
     */
    private void readPayload(AsynchronousSocketChannel channel, int length, CompletableFuture<ResponseProto.ResponseDTO> response) {
        ByteBuffer payloadBuf = ByteBuffer.allocate(length);
        channel.read(payloadBuf, null, new CompletionHandler<Integer, Void>() {
            @Override
            public void completed(Integer bytesRead, Void att) {
                if (bytesRead == -1) {
                    response.completeExceptionally(new IOException("Stream closed before full payload received"));
                    return;
                }
                if (payloadBuf.hasRemaining()) {
                    channel.read(payloadBuf, null, this);
                } else {
                    try {
                        payloadBuf.flip();
                        ResponseProto.ResponseDTO resp = ResponseProto.ResponseDTO.parseFrom(payloadBuf);
                        response.complete(resp);
                    } catch (Exception e) {
                        response.completeExceptionally(e);
                    }
                }
            }

            @Override
            public void failed(Throwable t, Void att) {
                response.completeExceptionally(t);
            }
        });
    }

}
