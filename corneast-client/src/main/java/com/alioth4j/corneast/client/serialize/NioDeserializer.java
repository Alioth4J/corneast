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

package com.alioth4j.corneast.client.serialize;

import com.alioth4j.corneast.common.proto.ResponseProto;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

/**
 * Deserializer implemented with NIO.
 * <p>
 * It's a singleton.
 *
 * @author Alioth Null
 */
public final class NioDeserializer extends AbstractDeserializer {

    private static volatile NioDeserializer instance;

    private NioDeserializer() {
    }

    public static NioDeserializer getInstance() {
        if (instance == null) {
            synchronized (NioDeserializer.class) {
                if (instance == null) {
                    instance = new NioDeserializer();
                }
            }
        }
        return instance;
    }

    /**
     * Deserializes for NIO.
     * @param channel NIO channel
     * @return response object
     * @throws IOException thrown when error occurs during io
     */
    public ResponseProto.ResponseDTO deserialize(ReadableByteChannel channel) throws IOException {
        int result = 0;
        int shift  = 0;
        ByteBuffer oneByte = ByteBuffer.allocate(1);
        for (int i = 0; i < MAX_VARINT32_BYTES; i++) {
            oneByte.clear();
            int read = channel.read(oneByte);
            if (read != 1) {
                throw new IOException("Premature end of stream while reading length prefix");
            }
            oneByte.flip();
            int b = oneByte.get() & 0xFF;
            result |= (b & 0x7F) << shift;
            if ((b & 0x80) == 0) {
                break;
            }
            shift += 7;
        }

        if (result < 0) {
            throw new IOException("Negative payload length: " + result);
        }
        if (result > MAX_PAYLOAD_SIZE) {
            throw new IOException("Payload too large: " + result);
        }

        ByteBuffer payloadBuf = ByteBuffer.allocate(result);
        while (payloadBuf.hasRemaining()) {
            int read = channel.read(payloadBuf);
            if (read == -1) {
                throw new IOException("Stream ended before full payload could be read");
            }
        }
        return ResponseProto.ResponseDTO.parseFrom(payloadBuf.flip());
    }

}
