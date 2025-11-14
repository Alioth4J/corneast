package com.alioth4j.corneast_client.send;

import com.alioth4j.corneast_core.proto.RequestProto;
import com.alioth4j.corneast_core.proto.ResponseProto;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Sends requests and receives responses with socket.
 *
 * @author Alioth Null
 */
public class CorneastSocketClient {

    private static final String host = "127.0.0.1";
    private static final int port = 8088;

    public static ResponseProto.ResponseDTO send(RequestProto.RequestDTO requestDTO) throws IOException {
        byte[] requestDTOBytes = requestDTO.toByteArray();
        byte[] preVarint32 = encodeVarint32(requestDTOBytes.length);
        byte[] requestData = new byte[requestDTOBytes.length + preVarint32.length];
        System.arraycopy(preVarint32, 0, requestData, 0, preVarint32.length);
        System.arraycopy(requestDTOBytes, 0, requestData, preVarint32.length, requestDTOBytes.length);

        // TODO exception handling
        try (Socket socket = new Socket(host, port)) {
//            // debug
//            socket.setSoTimeout(5000);

            OutputStream out = socket.getOutputStream();
            out.write(requestData);
            out.flush();

            InputStream in = socket.getInputStream();
            byte[] payload = readPayload(in);
            return ResponseProto.ResponseDTO.parseFrom(payload);
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Reads payload from input stream.
     * Reads varint32 length prefix first, then the payload according to the length.
     *
     * The length prefix will be discarded after use.
     *
     * @param in input stream from socket
     * @return payload in byte array
     * @throws IOException thrown when socket closes while reading
     */
    // TODO optimize
    private static byte[] readPayload(InputStream in) throws IOException {
        int shift = 0;
        int result = 0;
        int prefixLen = 0;
        while (prefixLen < 5) {
            int b = in.read();
            if (b == -1) {
                throw new IOException("Stream closed while reading length prefix");
            }
            prefixLen++;
            result |= (b & 0x7F) << shift;
            if ((b & 0x80) == 0) {
                break;
            }
            shift += 7;
        }

        int payloadLen = result;
        byte[] payload = new byte[payloadLen];
        int read = 0;
        while (read < payloadLen) {
            int r = in.read(payload, read, payloadLen - read);
            if (r == -1) {
                throw new IOException("Stream closed while reading payload");
            }
            read += r;
        }
        return payload;
    }

    /**
     * Reads all the bytes in `InputStream`.
     * @param in input stream
     * @return byte array containing all bytes
     * @throws IOException error reading bytes
     */
    private static byte[] readAll(InputStream in) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int r;
        while ((r = in.read(buf)) > 0) {
            baos.write(buf, 0, r);
        }
        return baos.toByteArray();
    }

    /**
     * Encodes an integer into a varint32 format as used by Protobuf.
     *
     * @param value int to encode
     * @return length prefix
     */
    private static byte[] encodeVarint32(int value) {
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
     * Decodes a 32-bit varint, as used by protobuf, from the start of the
     * provided byte buffer.
     *
     * @param buf the byte array containing the varint32 prefix
     * @return a two-element int array: {@code [decodedValue, bytesConsumed]}
     * @throws IOException if the varint is malformed (no terminating byte
     *                     within 5 bytes) or the buffer is too short to contain
     *                     a complete 32-bit varint
     */
    private static int[] decodeVarint32(byte[] buf) throws IOException {
        int result = 0;
        int shift = 0;
        for (int i = 0; i < Math.min(5, buf.length); i++) {
            int b = buf[i] & 0xFF;
            result |= (b & 0x7F) << shift;
            if ((b & 0x80) == 0) {
                return new int[]{result, i + 1};
            }
            shift += 7;
        }
        throw new IOException("Malformed varint32 or buffer too short");
    }

}
