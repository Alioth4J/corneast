package com.alioth4j.corneast_core.netty;

import com.alioth4j.corneast_core.proto.RequestProto;
import com.alioth4j.corneast_core.proto.ResponseProto;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import com.google.common.util.concurrent.RateLimiter;
import jakarta.annotation.PostConstruct;
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
        if (rateLimiter.tryAcquire()) {
            ctx.fireChannelRead(requestDTO);
        } else {
            ctx.writeAndFlush(rateLimitedResponse);
            ctx.close();
        }
    }

}
