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
import com.alioth4j.corneast_common.proto.RequestProto;
import com.alioth4j.corneast_common.proto.ResponseProto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;

/**
 * Sends requests and receives responses with socket.
 *
 * @author Alioth Null
 */
public class CorneastSocketClient {

    private static final Logger log = LoggerFactory.getLogger(CorneastSocketClient.class);

    /* server host */
    private final String host;

    /* server port */
    private final int port;

    /**
     * Private constructor, invoked by <code>of()</code>.
     * @param host server host
     * @param port server port
     */
    private CorneastSocketClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * The only way to construct a <code>CorneastSocketClient</code> instance.
     * @param config config object
     * @return instance of <code>CorneastSocketClient</code>
     */
    public static CorneastSocketClient of(CorneastConfig config) {
        if (!config.validate()) {
            throw new IllegalArgumentException("CorneastConfig has not been completely set, current config: " + config);
        }
        return new CorneastSocketClient(config.getHost(), config.getPort());
    }

    /**
     * Sends requests to corneast-core, receiving responses.
     * @param requestDTO request object
     * @return response object
     * @throws IOException if socket read or write fails
     */
    public ResponseProto.ResponseDTO send(RequestProto.RequestDTO requestDTO) throws IOException {
        byte[] requestDTOBytes = requestDTO.toByteArray();
        byte[] preVarint32 = encodeVarint32(requestDTOBytes.length);
        byte[] requestData = new byte[requestDTOBytes.length + preVarint32.length];
        System.arraycopy(preVarint32, 0, requestData, 0, preVarint32.length);
        System.arraycopy(requestDTOBytes, 0, requestData, preVarint32.length, requestDTOBytes.length);

        try (Socket socket = new Socket(host, port)) {
//            // debug
//            socket.setSoTimeout(5000);

            OutputStream out = socket.getOutputStream();
            out.write(requestData);
            out.flush();

            InputStream in = socket.getInputStream();
            byte[] payload = readPayload(in);
            return ResponseProto.ResponseDTO.parseFrom(payload);
        } catch (IOException e) {
            log.error("I/O failure: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage(), e);
            throw e;
        }
    }

    private static final int MAX_VARINT32_BYTES = 5;

    // upper bound for a payload
    private static final int MAX_PAYLOAD_SIZE = 10 * 1024 * 1024; // 10MB

    /**
     * Reads varint32 prefix and the payload from the given stream,
     * returns the payload only.
     * @param in input stream
     * @return payload without prefix
     * @throws IOException if the stream ends prematurely or the length is invalid
     */
    private byte[] readPayload(InputStream in) throws IOException {
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

    /**
     * Encodes an integer into a varint32 format as used by Protobuf.
     *
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
