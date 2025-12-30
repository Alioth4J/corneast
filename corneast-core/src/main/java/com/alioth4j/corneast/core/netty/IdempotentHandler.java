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

package com.alioth4j.corneast.core.netty;

import com.alioth4j.corneast.common.operation.CorneastOperation;
import com.alioth4j.corneast.common.proto.RequestProto;
import com.alioth4j.corneast.common.proto.ResponseProto;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(IdempotentHandler.class);

    @Autowired
    @Qualifier("idempotentRedissonClient")
    private RedissonClient idempotentRedissonClient;

    private static final String testAndSetLuaScript = """
                                                      local v = redis.call("GET", KEYS[1])
                                                      if not v then redis.call("SET", KEYS[1], "1") end
                                                      return v
                                                      """;

    private static final ResponseProto.ResponseDTO idempotentResponse = ResponseProto.ResponseDTO.newBuilder()
                                                                                                 .setType(CorneastOperation.IDEMPOTENT)
                                                                                                 .build();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RequestProto.RequestDTO requestDTO) throws Exception {
        String id = requestDTO.getId();
        log.debug("Arrived at IdempotentHandler, id = {}", id);

        // id != null because of protobuf
        // String#intern in compile-time by java
        if (id == "") {
            // disable idempotence
            log.debug("Disabled idempotent, id = {}", id);
            ctx.fireChannelRead(requestDTO);
            return;
        }
        String exists = idempotentRedissonClient.getScript(StringCodec.INSTANCE).eval(RScript.Mode.READ_WRITE, testAndSetLuaScript, RScript.ReturnType.VALUE, List.of(id));
        if (exists == null) {
            log.debug("Passed IdempotentHandler, id = {}", id);
            ctx.fireChannelRead(requestDTO);
        } else {
            log.debug("Being Idempotented, id = {}", id);
            ctx.writeAndFlush(idempotentResponse);
            ctx.close();
        }
    }

}
