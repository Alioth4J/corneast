module com.alioth4j.corneast.client {
    requires com.alioth4j.corneast.common;
    requires eureka.client;
    requires org.slf4j;

    exports com.alioth4j.corneast.client.config;
    exports com.alioth4j.corneast.client.eureka;
    exports com.alioth4j.corneast.client.exception;
    exports com.alioth4j.corneast.client.request;
    exports com.alioth4j.corneast.client.send;
}