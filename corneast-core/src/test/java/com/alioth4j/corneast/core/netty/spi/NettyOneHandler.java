/*
 * Corneast
 * Copyright (C) 2026 Alioth Null
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

package com.alioth4j.corneast.core.netty.spi;

import com.alioth4j.corneast.common.proto.RequestProto;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
public class NettyOneHandler extends SimpleChannelInboundHandler<RequestProto.RequestDTO> implements NettyCustomHandler {

    private static final Logger log = LoggerFactory.getLogger(NettyOneHandler.class);

    public NettyOneHandler() {
    }

    @Override
    public int getOrder() {
        return 1;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RequestProto.RequestDTO requestDTO) throws Exception {
        log.info("Netty custom handler #{} is invoked", getOrder());
        ctx.fireChannelRead(requestDTO);
    }

}
