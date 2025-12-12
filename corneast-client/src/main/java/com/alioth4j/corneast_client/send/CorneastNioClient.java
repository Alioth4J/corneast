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

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * NIO client for sending requests and receiving responses.
 *
 * @author Alioth Null
 */
public class CorneastNioClient {

    private final String host;
    private final int port;

    private CorneastNioClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public static CorneastNioClient of(CorneastConfig config) {
        if (!config.validate()) {
            throw new IllegalArgumentException("CorneastConfig has not been completely set, current config: " + config);
        }
        return new CorneastNioClient(config.getHost(), config.getPort());
    }
    
    public ResponseProto.ResponseDTO send(RequestProto.RequestDTO requestDTO) throws IOException {
        byte[] requestBinary = SerializeUtil.serialize(requestDTO);
        try (SocketChannel channel = SocketChannel.open()) {
            // TODO
            channel.configureBlocking(true);
            channel.connect(new InetSocketAddress(host, port));

            ByteBuffer writeBuf = ByteBuffer.wrap(requestBinary);
            while (writeBuf.hasRemaining()) {
                channel.write(writeBuf);
            }

            return SerializeUtil.deserialize(channel);
        }
    }

}
