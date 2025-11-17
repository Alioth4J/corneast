package com.alioth4j.corneast_core.shutdown;

import com.alioth4j.corneast_core.ringbuffer.ReduceDisruptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Shutdown task of disruptor.
 *
 * @author Alioth Null
 */
@Component
public class DisruptorShutdownTask implements ShutdownTask {

    private static final Logger log = LoggerFactory.getLogger(DisruptorShutdownTask.class);

    @Autowired
    private ReduceDisruptor reduceDisruptor;

    @Override
    public void shutdown() {
        reduceDisruptor.shutdown(log);
    }

    @Override
    public String getComponentName() {
        return "Disruptor";
    }

}
