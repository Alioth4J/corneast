package com.alioth4j.corneast_core.netty;

import com.alioth4j.corneast_core.proto.RequestProto;
import com.alioth4j.corneast_core.proto.ResponseProto;
import com.alioth4j.corneast_core.strategy.RequestHandlingStrategy;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Route requests for netty.
 */
@Component
@ChannelHandler.Sharable
public class RequestRouteHandler extends SimpleChannelInboundHandler<RequestProto.RequestDTO> {

    @Autowired
    private Map<String/* beanName */, RequestHandlingStrategy/* object of strategy */> requestHandlingStrategyMap;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RequestProto.RequestDTO requestDTO) throws Exception {
        // choose the strategy
        String requestType = requestDTO.getType();
        RequestHandlingStrategy requestHandlingStrategy = requestHandlingStrategyMap.get(requestType);
        // handle
        CompletableFuture<ResponseProto.ResponseDTO> responseCompletableFuture = requestHandlingStrategy.handle(requestDTO);
        // write and flush the response
        responseCompletableFuture.whenComplete((responseDTO, t) -> {
            channelHandlerContext.writeAndFlush(responseDTO);
        });
    }

}
