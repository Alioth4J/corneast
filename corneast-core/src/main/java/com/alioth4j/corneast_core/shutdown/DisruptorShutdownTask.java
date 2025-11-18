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
