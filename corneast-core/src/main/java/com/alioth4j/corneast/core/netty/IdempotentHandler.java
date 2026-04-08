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

package com.alioth4j.corneast.core.netty;

import com.alioth4j.corneast.common.operation.CorneastOperation;
import com.alioth4j.corneast.common.proto.RequestProto;
import com.alioth4j.corneast.common.proto.ResponseProto;
import com.alioth4j.corneast.core.exception.CorneastHandleException;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.ByteArrayCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
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

    @Value("${idempotent.ttl:10}")
    private String ttl;

    private static final String testAndSetLuaScript = """
                                                      local v = redis.call("GET", KEYS[1])
                                                      if not v then redis.call("SET", KEYS[1], "1", "EX", ARGV[1]) end
                                                      return v
                                                      """;

    private static final ResponseProto.ResponseDTO idempotentResponse = ResponseProto.ResponseDTO.newBuilder()
                                                                                                 .setType(CorneastOperation.IDEMPOTENT)
                                                                                                 .build();

    private static final byte[] idempotentedValue = new byte[]{'1'};

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RequestProto.RequestDTO requestDTO) throws Exception {
        String id = requestDTO.getId();
        log.debug("Arrived at IdempotentHandler, id = {}", id);

        if (id.isEmpty()) {
            // disable idempotence
            log.debug("Disabled idempotent, id = {}", id);
            ctx.fireChannelRead(requestDTO);
            return;
        }
        byte[] existValue = idempotentRedissonClient.getScript(ByteArrayCodec.INSTANCE).eval(RScript.Mode.READ_WRITE, testAndSetLuaScript, RScript.ReturnType.VALUE, List.of(id), ttl.getBytes(StandardCharsets.UTF_8));
        if (existValue == null) {
            log.debug("Passed IdempotentHandler, id = {}", id);
            ctx.fireChannelRead(requestDTO);
        } else {
            log.debug("Being Idempotented, id = {}", id);
            int maxCount = 3;
            while (Arrays.equals(idempotentedValue, existValue) && maxCount-- > 0) {
                Thread.sleep(200);
                existValue = idempotentRedissonClient.<byte[]>getBucket(id, ByteArrayCodec.INSTANCE).get();
            }
            if (Arrays.equals(idempotentedValue, existValue)) {
                log.error("Unable to get idempotented response, id = {}", id);
                throw new CorneastHandleException("Unable to get idempotent response, id = " + id);
            }
            ctx.writeAndFlush(ResponseProto.ResponseDTO.parseFrom(existValue));
            ctx.close();
        }
    }

}
