package com.alioth4j.corneast_core.netty;

import com.alioth4j.corneast_core.proto.ReduceProto;
import com.alioth4j.corneast_core.service.ReduceService;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@ChannelHandler.Sharable
public class ReduceServiceHandler extends SimpleChannelInboundHandler<ReduceProto.ReduceReqDTO> {

    private ReduceService reduceService;

    public ReduceServiceHandler(ReduceService reduceService) {
        this.reduceService = reduceService;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ReduceProto.ReduceReqDTO reduceReqDTO) throws Exception {
        CompletableFuture<ReduceProto.ReduceRespDTO> future = reduceService.reduce(reduceReqDTO);
        future.whenComplete((reduceRespDTO, ignored) -> {
            ctx.writeAndFlush(reduceRespDTO);
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}
