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
import com.alioth4j.corneast.client.serialize.BioDeserializer;
import com.alioth4j.corneast.client.serialize.ProtobufSerializer;
import com.alioth4j.corneast.common.proto.RequestProto;
import com.alioth4j.corneast.common.proto.ResponseProto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Sends requests and receives responses with socket.
 * <p>
 * BIO.
 *
 * @author Alioth Null
 */
public class CorneastBioClient implements Closeable {

    private static final Logger log = LoggerFactory.getLogger(CorneastBioClient.class);

    private final String host;
    private final int port;

    // keep-alive socket
    private Socket socket;
    private OutputStream outputStream;
    private InputStream inputStream;

    private final Object ioLock = new Object();

    private volatile boolean closed = false;

    /**
     * Private constructor, invoked by <code>of()</code>.
     */
    private CorneastBioClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * The only way to construct a <code>CorneastBioClient</code> instance.
     * @param config config object
     * @return instance of <code>CorneastBioClient</code>
     */
    public static CorneastBioClient of(CorneastConfig config) throws IOException {
        if (!config.validate()) {
            throw new IllegalArgumentException("CorneastConfig has not been completely set, current config: " + config);
        }

        CorneastBioClient instance = new CorneastBioClient(config.getHost(), config.getPort());
        instance.openConnection();
        log.info("Initialized CorneastBioClient instance");
        return instance;
    }

    /**
     * Sends requests to corneast-core, receiving responses.
     */
    public ResponseProto.ResponseDTO send(RequestProto.RequestDTO requestDTO) throws IOException {
        byte[] binaryRequest = ProtobufSerializer.getInstance().serialize(requestDTO);
        synchronized (ioLock) {
            ensureConnected();

            outputStream.write(binaryRequest);
            outputStream.flush();
            return BioDeserializer.getInstance().deserialize(inputStream);
        }
    }

    private void ensureConnected() throws IOException {
        if (closed) {
            throw new IOException("Socket has been closed");
        }
        if (socket != null && !socket.isClosed()) {
            return;
        }
        resetConnection();
    }

    private void resetConnection() throws IOException {
        closeConnection();
        openConnection();
    }

    private void closeConnection() {
        closeQuietly(outputStream);
        closeQuietly(inputStream);
        closeQuietly(socket);
        outputStream = null;
        inputStream = null;
        socket = null;
    }

    private void closeQuietly(Closeable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (IOException ignored) {
        }
    }

    private void openConnection() throws IOException {
        socket = new Socket();
        socket.setKeepAlive(true);
        socket.setTcpNoDelay(true);
        socket.connect(new InetSocketAddress(host, port));
        outputStream = new BufferedOutputStream(socket.getOutputStream());
        inputStream = socket.getInputStream();
    }

    @Override
    public void close() {
        synchronized (ioLock) {
            closed = true;
            closeConnection();
        }
    }

}
