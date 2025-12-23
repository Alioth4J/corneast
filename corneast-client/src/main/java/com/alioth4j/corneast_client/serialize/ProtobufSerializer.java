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

import com.alioth4j.corneast_common.proto.RequestProto;

/**
 * This class serializes a protobuf request object
 * to binary.
 * <p>
 * It's singleton.
 *
 * @author Alioth Null
 */
public final class ProtobufSerializer implements Serializer {

    private static volatile ProtobufSerializer instance;

    private ProtobufSerializer() {
    }

    public static ProtobufSerializer getInstance() {
        if (instance == null) {
            synchronized (ProtobufSerializer.class) {
                if (instance == null) {
                    instance = new ProtobufSerializer();
                }
            }
        }
        return instance;
    }

    /**
     * Switch a <code>requestDTO</code> to binary request that includes varint32 prefix.
     * @param request request object
     * @return the complete binary request
     */
    @Override
    public byte[] serialize(RequestProto.RequestDTO request) {
        byte[] requestDTOBytes = request.toByteArray();
        byte[] preVarint32 = encodeVarint32(requestDTOBytes.length);
        byte[] binaryRequest = new byte[requestDTOBytes.length + preVarint32.length];
        System.arraycopy(preVarint32, 0, binaryRequest, 0, preVarint32.length);
        System.arraycopy(requestDTOBytes, 0, binaryRequest, preVarint32.length, requestDTOBytes.length);
        return binaryRequest;
    }

    /**
     * Encodes an integer into a varint32 format as used by Protobuf.
     * @param value int to encode
     * @return length prefix
     */
    private byte[] encodeVarint32(int value) {
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

}
