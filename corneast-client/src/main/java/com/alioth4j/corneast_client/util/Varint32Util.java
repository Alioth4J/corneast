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

package com.alioth4j.corneast_client.util;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Util class for operating varint32.
 *
 * @author Alioth Null
 */
public final class Varint32Util {

    private static final int MAX_VARINT32_BYTES = 5;

    // upper bound for a payload
    private static final int MAX_PAYLOAD_SIZE = 10 * 1024 * 1024; // 10MB

    
    private Varint32Util() {
    }


    /**
     * Encodes an integer into a varint32 format as used by Protobuf.
     *
     * @param value int to encode
     * @return length prefix
     */
    public static byte[] encode(int value) {
        byte[] buffer = new byte[5];
        int position = 0;
        while ((value & ~0x7F) != 0) {
            buffer[position++] = (byte)((value & 0x7F) | 0x80);
            value >>>= 7;
        }
        buffer[position++] = (byte)(value);
        byte[] result = new byte[position];
        System.arraycopy(buffer, 0, result, 0, position);
        return result;
    }

    /**
     * Reads varint32 prefix and the payload from the given stream,
     * returns the payload only.
     * @param in input stream
     * @return payload without prefix
     * @throws IOException if the stream ends prematurely or the length is invalid
     */
    public static byte[] getPayload(InputStream in) throws IOException {
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
        return payload;
    }

}
