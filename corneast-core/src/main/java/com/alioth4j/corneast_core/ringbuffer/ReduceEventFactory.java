package com.alioth4j.corneast_core.ringbuffer;

import com.lmax.disruptor.EventFactory;

/**
 * Factory class for creating ReduceEvent.
 *
 * @author Alioth Null
 */
public class ReduceEventFactory implements EventFactory<ReduceEvent> {

    @Override
    public ReduceEvent newInstance() {
        return new ReduceEvent();
    }

}
