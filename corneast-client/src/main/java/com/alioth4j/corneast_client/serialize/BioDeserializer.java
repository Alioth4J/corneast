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

package com.alioth4j.corneast_client.serialize;

import com.alioth4j.corneast_common.proto.ResponseProto;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Deserializer implemented with BIO.
 * <p>
 * It's a singleton.
 *
 * @author Alioth Null
 */
public final class BioDeserializer extends AbstractDeserializer {

    private static volatile BioDeserializer instance;

    private BioDeserializer() {
    }

    public static BioDeserializer getInstance() {
        if (instance == null) {
            synchronized (BioDeserializer.class) {
                if (instance == null) {
                    instance = new BioDeserializer();
                }
            }
        }
        return instance;
    }

    /**
     * Deserializes for BIO.
     * @param in input stream
     * @return response object
     * @throws IOException thrown when error occurs during io
     */
    public ResponseProto.ResponseDTO deserialize(InputStream in) throws IOException {
        // Wrap the input in a BufferedInputStream to reduce system calls.
        BufferedInputStream buf = (in instanceof BufferedInputStream)
                ? (BufferedInputStream) in
                : new BufferedInputStream(in);
        DataInputStream dis = new DataInputStream(buf);

        // read the varint32 length prefix
        int result = 0;
        int shift = 0;
        for (int i = 0; i < MAX_VARINT32_BYTES; i++) {
            int b = dis.readUnsignedByte();
            result |= (b & 0x7F) << shift;
            if ((b & 0x80) == 0) {
                break;
            }
            shift += 7;
        }

        // validate the length
        if (result < 0) {
            throw new IOException("Negative payload length: " + result);
        }
        if (result > MAX_PAYLOAD_SIZE) {
            throw new IOException("Payload too large: " + result);
        }

        // read the full payload
        byte[] payload = new byte[result];
        dis.readFully(payload);
        return ResponseProto.ResponseDTO.parseFrom(payload);
    }

}
