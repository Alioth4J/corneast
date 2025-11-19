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

package com.alioth4j.corneast_core.netty;

import com.alioth4j.corneast_common.operation.CorneastOperation;
import com.alioth4j.corneast_common.proto.RequestProto;
import com.alioth4j.corneast_common.proto.ResponseProto;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Idempotent handler of Netty.
 *
 * @author Alioth Null
 */
@Component
@ChannelHandler.Sharable
public class IdempotentHandler extends SimpleChannelInboundHandler<RequestProto.RequestDTO> {

    @Autowired
    @Qualifier("idempotentRedissonClient")
    private RedissonClient idempotentRedissonClient;

    private static final String readLuaScript = """
                                                return redis.call("GET", KEYS[1])
                                                """;

    private static final String writeLuaScript = """
                                                 redis.call("SET", KEYS[1], "1")
                                                 """;

    private static final ResponseProto.ResponseDTO idempotentResponse = ResponseProto.ResponseDTO.newBuilder()
                                                                                                 .setType(CorneastOperation.IDEMPOTENT)
                                                                                                 .build();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RequestProto.RequestDTO requestDTO) throws Exception {
        String id = requestDTO.getId();
        // id != null because of protobuf
        // String#intern in compile-time by java
        if (id == "") {
            // disable idempotence
            ctx.fireChannelRead(requestDTO);
            return;
        }
        String exists = idempotentRedissonClient.getScript(StringCodec.INSTANCE).eval(RScript.Mode.READ_ONLY, readLuaScript, RScript.ReturnType.VALUE, List.of(id));
        if (exists == null) {
            idempotentRedissonClient.getScript(StringCodec.INSTANCE).eval(RScript.Mode.READ_WRITE, writeLuaScript, RScript.ReturnType.VALUE, List.of(id));
            ctx.fireChannelRead(requestDTO);
        } else {
            ctx.writeAndFlush(idempotentResponse);
            ctx.close();
        }
    }

}
