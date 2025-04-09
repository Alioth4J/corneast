package com.alioth4j.corneast_test;

import com.alioth4j.corneast_core.proto.ReduceProto;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Netty client for test.
 */
@Deprecated
public class NettyTests {

    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<Channel>() {
                        @Override
                        public void initChannel(Channel ch) {
                            ch.pipeline().addLast(new ProtobufDecoder(ReduceProto.ReduceRespDTO.getDefaultInstance()));
                            ch.pipeline().addLast(new ProtobufEncoder());
                            ch.pipeline().addLast(new ClientReduceHandler());
                        }
                    });
            Channel channel = bootstrap.connect("127.0.0.1", 8088).sync().channel();

            String key = DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDate.now());
            ReduceProto.ReduceReqDTO reduceReq = ReduceProto.ReduceReqDTO.newBuilder()
                    .setKey(key)
                    .build();
            channel.writeAndFlush(reduceReq).sync();
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }

}
