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
import com.alioth4j.corneast_client.util.SerializeUtil;
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
        byte[] binaryRequest = SerializeUtil.serialize(requestDTO);

        try (Socket socket = new Socket(host, port)) {
//            // debug
//            socket.setSoTimeout(5000);

            OutputStream out = socket.getOutputStream();
            out.write(binaryRequest);
            out.flush();

            InputStream in = socket.getInputStream();
            return SerializeUtil.deserialize(in);
        } catch (IOException e) {
            log.error("I/O failure: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage(), e);
            throw e;
        }
    }

}
