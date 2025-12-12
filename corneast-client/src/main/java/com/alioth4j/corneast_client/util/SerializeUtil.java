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

import com.alioth4j.corneast_common.proto.RequestProto;
import com.alioth4j.corneast_common.proto.ResponseProto;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.ReadableByteChannel;

/**
 * Util class for serialize request and response.
 *
 * @author Alioth Null
 */
public final class SerializeUtil {

    private SerializeUtil() {
    }

    /**
     * Switch a <code>requestDTO</code> to binary request that includes varint32 prefix.
     * @param requestDTO request object
     * @return the complete binary request
     */
    public static byte[] serialize(RequestProto.RequestDTO requestDTO) {
        byte[] requestDTOBytes = requestDTO.toByteArray();
        byte[] preVarint32 = Varint32Util.encode(requestDTOBytes.length);
        byte[] binaryRequest = new byte[requestDTOBytes.length + preVarint32.length];
        System.arraycopy(preVarint32, 0, binaryRequest, 0, preVarint32.length);
        System.arraycopy(requestDTOBytes, 0, binaryRequest, preVarint32.length, requestDTOBytes.length);
        return binaryRequest;
    }

    /**
     * Switch input stream to response object.
     * @param in input stream including varint32 prefix
     * @return response object
     */
    public static ResponseProto.ResponseDTO deserialize(InputStream in) throws IOException {
        byte[] payload = Varint32Util.getPayload(in);
        return ResponseProto.ResponseDTO.parseFrom(payload);
    }

    public static ResponseProto.ResponseDTO deserialize(ReadableByteChannel channel) throws IOException {
        byte[] payload = Varint32Util.getPayload(channel);
        return ResponseProto.ResponseDTO.parseFrom(payload);
    }

}
