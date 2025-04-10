package com.alioth4j.corneast_core.ringbuffer;

import com.lmax.disruptor.EventFactory;

public class ReduceEventFactory implements EventFactory<ReduceEvent> {

    @Override
    public ReduceEvent newInstance() {
        return new ReduceEvent();
    }

}
