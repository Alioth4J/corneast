package com.alioth4j.corneast_core.netty;

import com.alioth4j.corneast_core.proto.ReduceProto;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

@Component
public class NettyServer {

    @Value("${netty.server.port:8088}")
    private int port;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    private ChannelFuture channelFuture;

    private ReduceServiceHandler reduceServiceHandler;

    public NettyServer(ReduceServiceHandler reduceServiceHandler) {
        this.reduceServiceHandler = reduceServiceHandler;
    }

    @PostConstruct
    public void start() {
        new Thread(() -> {
            // TODO adjust params
            bossGroup = new NioEventLoopGroup(1);
            workerGroup = new NioEventLoopGroup();
            try {
                ServerBootstrap bootstrap = new ServerBootstrap();
                bootstrap.group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class)
                        .handler(new LoggingHandler(LogLevel.INFO))
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel ch) {
                                // protobuf handler
                                ch.pipeline().addLast(new ProtobufDecoder(ReduceProto.ReduceReqDTO.getDefaultInstance()));
                                ch.pipeline().addLast(new ProtobufEncoder());

                                // reduce handler
                                ch.pipeline().addLast(reduceServiceHandler);
                            }
                        });
                channelFuture = bootstrap.bind(port).sync();
                channelFuture.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                shutdown();
            }
        }).start();
    }

    @PreDestroy
    public void shutdown() {
        if (channelFuture != null) {
            channelFuture.channel().close();
        }
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
    }

}
