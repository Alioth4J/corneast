/*
 * Corneast
 * Copyright (C) 2025 Alioth Null
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

package com.alioth4j.corneast_core.netty;

import com.alioth4j.corneast_common.proto.RequestProto;
import com.alioth4j.corneast_core.netty.spi.NettyCustomHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Netty server.
 *
 * @author Alioth Null
 */
@Component
public class NettyServer {

    private static final Logger log = LoggerFactory.getLogger(NettyServer.class);

    @Value("${netty.server.port:8088}")
    private int port;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    private ChannelFuture channelFuture;

    private Thread serverThread;

    @Autowired
    private RateLimitingHandler rateLimitingHandler;

    @Autowired
    private IdempotentHandler idempotentHandler;

    private List<NettyCustomHandler> customHandlers;

    @Autowired
    private RequestRouteHandler requestRouteHandler;

    @PostConstruct
    public void start() {
        log.info("Netty server is starting...");
        initCustomHandlers();
        serverThread = new Thread(() -> {
            bossGroup = new NioEventLoopGroup(1);
            workerGroup = new NioEventLoopGroup(80); // 4 * (CPU cores)
            try {
                ServerBootstrap bootstrap = new ServerBootstrap();
                bootstrap.group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class)
                        .option(ChannelOption.SO_BACKLOG, 1024)
                        .option(ChannelOption.SO_REUSEADDR, true)
                        .handler(new LoggingHandler(LogLevel.INFO))
                        .childOption(ChannelOption.SO_KEEPALIVE, false)
                        .childOption(ChannelOption.TCP_NODELAY, true)
                        .childOption(ChannelOption.ALLOCATOR, new PooledByteBufAllocator(true, 40, 40, 8192, 11))
                        .childOption(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator(64, 8192, 65535))
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel ch) {
                                // rate limiting
                                ch.pipeline().addLast(rateLimitingHandler);

                                // protobuf handler
                                ch.pipeline().addLast(new ProtobufVarint32FrameDecoder());
                                ch.pipeline().addLast(new ProtobufDecoder(RequestProto.RequestDTO.getDefaultInstance()));
                                ch.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
                                ch.pipeline().addLast(new ProtobufEncoder());

                                // idempotent handler
                                ch.pipeline().addLast(idempotentHandler);

                                // custom handlers
                                for (ChannelHandler customHandler : customHandlers) {
                                    ch.pipeline().addLast(customHandler);
                                }

                                // route handler
                                ch.pipeline().addLast(requestRouteHandler);
                            }
                        });
                channelFuture = bootstrap.bind(port).sync();
                channelFuture.channel().closeFuture().sync();
                log.info("Netty server channel closed, shutting down");
            } catch (InterruptedException e) {
                log.warn("Interruption occurs in netty server thread", e);
                Thread.currentThread().interrupt();
            } finally {
                shutdownBossAndWorkerGroups(NettyServer.log);
            }
        });
        serverThread.setName("Netty-Server");
        serverThread.setDaemon(false);
        serverThread.start();
    }

    /**
     * Init custom child handlers using SPI.
     * <code>customHandlers</code> is non-null, unmodifiable <code>List</code>.
     */
    private void initCustomHandlers() {
        customHandlers = ServiceLoader.load(NettyCustomHandler.class)
                .stream()
                .map(ServiceLoader.Provider::get)
                .sorted(Comparator.comparingInt(NettyCustomHandler::getOrder))
                .collect(Collectors.toList());
        customHandlers = Collections.unmodifiableList(customHandlers);

        // logging
        if (log.isInfoEnabled()) {
            if (customHandlers.isEmpty()) {
                log.info("No custom netty handlers found via SPI");
            } else {
                String handlerNames = customHandlers.stream()
                        .map(handler -> handler.getClass().getCanonicalName())
                        .collect(Collectors.joining(", "));
                log.info("Initialized {} netty custom handler(s): {}", customHandlers.size(), handlerNames);
            }
        }
    }

    /**
     * Shutdown netty server.
     * @param log Logger object to use
     */
    public void shutdown(Logger log) {
        // channelFuture
        if (channelFuture != null && channelFuture.channel() != null && channelFuture.channel().isOpen()) {
            channelFuture.channel().close();
        }

        // defensive code: serverThread
        if (serverThread != null && serverThread.isAlive()) {
            try {
                serverThread.join(5000);
                if (serverThread.isAlive()) {
                    serverThread.interrupt();
                    serverThread.join(1000);
                }
            } catch (InterruptedException e) {
                log.warn("Interrupted when joining serverThread", e);
                Thread.currentThread().interrupt();
                if (serverThread != null) {
                    serverThread.interrupt();
                }
            }
        }

        // defensive code: bossGroup and workerGroup
        shutdownBossAndWorkerGroups(log);
    }

    /**
     * Shutdown bossGroup and workerGroup
     * @param log Logger object to use
     */
    private void shutdownBossAndWorkerGroups(Logger log) {
        // boss group
        if (bossGroup != null) {
            try {
                bossGroup.shutdownGracefully(0, 5, TimeUnit.SECONDS).sync();
            } catch (InterruptedException e) {
                log.warn("Interrupted when shutting down BossGroup", e);
                Thread.currentThread().interrupt();
                bossGroup.shutdownGracefully();
            }
        }

        // worker group
        if (workerGroup != null) {
            try {
                workerGroup.shutdownGracefully(0, 5, TimeUnit.SECONDS).sync();
            } catch (InterruptedException e) {
                log.warn("Interrupted when shutting down WorkerGroup", e);
                Thread.currentThread().interrupt();
                workerGroup.shutdownGracefully();
            }
        }
    }

}
