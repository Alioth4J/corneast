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
import com.alioth4j.corneast.client.serialize.NioDeserializer;
import com.alioth4j.corneast.client.serialize.ProtobufSerializer;
import com.alioth4j.corneast.client.util.IOUtil;
import com.alioth4j.corneast.common.proto.RequestProto;
import com.alioth4j.corneast.common.proto.ResponseProto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * NIO client for sending requests and receiving responses.
 *
 * @author Alioth Null
 */
public class CorneastNioClient implements Closeable {

    private static final Logger log = LoggerFactory.getLogger(CorneastNioClient.class);

    private final String host;
    private final int port;

    private SocketChannel socketChannel;

    private volatile boolean closed = false;

    private final Object ioLock = new Object();

    private CorneastNioClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public static CorneastNioClient of(CorneastConfig config) throws IOException {
        if (!config.validate()) {
            throw new IllegalArgumentException("CorneastConfig has not been completely set, current config: " + config);
        }
        CorneastNioClient instance = new CorneastNioClient(config.getHost(), config.getPort());
        instance.openConnection();
        log.info("SocketChannel has been initialized");
        return instance;
    }
    
    public ResponseProto.ResponseDTO send(RequestProto.RequestDTO requestDTO) throws IOException {
        byte[] requestBinary = ProtobufSerializer.getInstance().serialize(requestDTO);
        synchronized (ioLock) {
            ensureConnected();
            ByteBuffer writeBuf = ByteBuffer.wrap(requestBinary);
            while (writeBuf.hasRemaining()) {
                socketChannel.write(writeBuf);
            }
            return NioDeserializer.getInstance().deserialize(socketChannel);
        }
    }

    private void ensureConnected() throws IOException {
        if (closed) {
            throw new IOException("SocketChannel has been closed");
        }
        if (socketChannel != null && socketChannel.isConnected()) {
            return;
        }
        resetConnection();
    }

    private void resetConnection() throws IOException {
        closeConnection();
        openConnection();
    }

    private void closeConnection() {
        IOUtil.closeQuietly(socketChannel);
        socketChannel = null;
    }

    private void openConnection() throws IOException {
        socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(true);
        socketChannel.socket().setKeepAlive(true);
        socketChannel.socket().setTcpNoDelay(true);
        socketChannel.socket().connect(new InetSocketAddress(host, port));
    }

    @Override
    public void close() {
        synchronized (ioLock) {
            closed = true;
            closeConnection();
        }
    }

}
