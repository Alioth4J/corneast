package com.alioth4j.corneast_core.netty;

import com.alioth4j.corneast_core.proto.RequestProto;
import com.alioth4j.corneast_core.proto.ResponseProto;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
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

    @Autowired
    @Qualifier("idempotentRedissonClient")
    private RedissonClient idempotentRedissonClient;

    private static final String readLuaScript = """
                                                return redis.call("SISMEMBER", "idempotent", KEYS[1])
                                                """;

    private static final String writeLuaScript = """
                                                 redis.call("SADD", "idempotent", KEYS[1])
                                                 """;

    private static final ResponseProto.ResponseDTO idempotentResponse = ResponseProto.ResponseDTO.newBuilder()
                                                                                                 .setType("idempotent")
                                                                                                 .build();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RequestProto.RequestDTO requestDTO) throws Exception {
        String id = requestDTO.getId();
        long exists = idempotentRedissonClient.getScript().eval(RScript.Mode.READ_ONLY, readLuaScript, RScript.ReturnType.VALUE, List.of(id));
        if (exists > 0) {
            ctx.writeAndFlush(idempotentResponse);
            ctx.close();
        } else {
            idempotentRedissonClient.getScript(StringCodec.INSTANCE).eval(RScript.Mode.READ_WRITE, writeLuaScript, RScript.ReturnType.VALUE, List.of(id));
            ctx.fireChannelRead(requestDTO);
        }
    }

}
