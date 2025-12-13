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

import com.alioth4j.corneast_common.proto.RequestProto;
import com.alioth4j.corneast_common.proto.ResponseProto;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import com.google.common.util.concurrent.RateLimiter;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Rate limiting component.
 *
 * @author Alioth Null
 */
@Component
@ChannelHandler.Sharable
public class RateLimitingHandler extends SimpleChannelInboundHandler<RequestProto.RequestDTO> {

    private static final Logger log = LoggerFactory.getLogger(RateLimitingHandler.class);

    @Value("${rateLimiting.permitsPerSecond}")
    private double permitsPerSecond;

    private RateLimiter rateLimiter;

    @PostConstruct
    public void init() {
        rateLimiter = RateLimiter.create(permitsPerSecond);
    }

    private ResponseProto.ResponseDTO rateLimitedResponse = ResponseProto.ResponseDTO.newBuilder()
                                                            .setType("rateLimited")
                                                            .build();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RequestProto.RequestDTO requestDTO) throws Exception {
        String id = requestDTO.getId();
        log.debug("Arrived at RateLimitingHandler, id = {}", id);
        if (rateLimiter.tryAcquire()) {
            log.debug("Passed RateLimitingHandler, id = {}", id);
            ctx.fireChannelRead(requestDTO);
        } else {
            log.debug("Being rate-limited, id = {}", id);
            ctx.writeAndFlush(rateLimitedResponse);
            ctx.close();
        }
    }

}
