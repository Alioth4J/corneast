module com.alioth4j.corneast.core {
    requires com.alioth4j.corneast.common;
    requires com.google.common;
    requires disruptor;
    requires io.netty.buffer;
    requires io.netty.codec;
    requires io.netty.common;
    requires io.netty.handler;
    requires io.netty.transport;
    requires io.netty.transport.rxtx;
    requires jakarta.annotation;
    requires org.aspectj.weaver;
    requires org.slf4j;
    requires redisson;
    requires spring.beans;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.context;

    exports com.alioth4j.corneast.core.netty.spi;

    uses com.alioth4j.corneast.core.netty.spi.NettyCustomHandler;
}