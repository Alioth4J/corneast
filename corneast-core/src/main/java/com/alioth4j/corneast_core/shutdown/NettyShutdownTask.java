package com.alioth4j.corneast_core.shutdown;

import com.alioth4j.corneast_core.netty.NettyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Shutdown task of netty server.
 *
 * @author Alioth Null
 */
@Component
public class NettyShutdownTask implements ShutdownTask {

    private static final Logger log = LoggerFactory.getLogger(NettyShutdownTask.class);

    @Autowired
    private NettyServer nettyServer;

    @Override
    public void shutdown() {
        nettyServer.shutdown(log);
    }

    @Override
    public String getComponentName() {
        return "NettyServer";
    }

}
