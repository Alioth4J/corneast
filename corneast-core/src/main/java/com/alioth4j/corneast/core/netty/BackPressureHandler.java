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

package com.alioth4j.corneast.core.netty;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Back pressure handler of Netty.
 * @author Alioth Null
 */
@Component
@ChannelHandler.Sharable
public class BackPressureHandler extends ChannelInboundHandlerAdapter {

    private static final Logger log = LoggerFactory.getLogger(BackPressureHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        read(ctx, "channel active failed");
        super.channelActive(ctx);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        if (ctx.channel().isActive()) {
            read(ctx, "normal read failed");
        }
        super.channelReadComplete(ctx);
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        read(ctx, "write buffer reached high water mark");
        super.channelWritabilityChanged(ctx);

    }

    private void read(ChannelHandlerContext ctx, String failureReason) {
        if (ctx.channel().isWritable()) {
            ctx.read();
        } else {
            log.warn("Channel {} is unwritable, cause: {}", ctx.channel().id(), failureReason);
        }
    }

}
