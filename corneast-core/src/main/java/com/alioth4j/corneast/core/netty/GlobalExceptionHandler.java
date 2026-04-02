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

import com.alioth4j.corneast.common.operation.CorneastOperation;
import com.alioth4j.corneast.common.proto.ResponseProto;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * try {} catch (Throwable ...)
 *
 * Just keep running, log it and reflect the failure in return.
 *
 * @author Alioth Null
 */
@Component
@ChannelHandler.Sharable
public class GlobalExceptionHandler extends ChannelInboundHandlerAdapter {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private static final ResponseProto.ResponseDTO errorResponse = ResponseProto.ResponseDTO.newBuilder()
                                                                                            .setType(CorneastOperation.ERROR)
                                                                                            .build();

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("Error occurred during inbound handlers", cause);
        ctx.writeAndFlush(errorResponse);
        ctx.close();
    }

}
