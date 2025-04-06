package com.alioth4j.corneast_test;

import com.alioth4j.corneast_core.proto.ReduceProto;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@Deprecated
public class ClientReduceHandler extends SimpleChannelInboundHandler<ReduceProto.ReduceRespDTO> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ReduceProto.ReduceRespDTO reduceRespDTO) throws Exception {
        System.out.println("Received reduce response:");
        System.out.println(reduceRespDTO);
        ctx.channel().close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}
