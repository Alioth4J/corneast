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

package com.alioth4j.corneast_core.strategy.impl;

import com.alioth4j.corneast_common.operation.CorneastOperation;
import com.alioth4j.corneast_common.proto.RequestProto;
import com.alioth4j.corneast_common.proto.ResponseProto;
import com.alioth4j.corneast_core.ringbuffer.ReduceDisruptor;
import com.alioth4j.corneast_core.strategy.RequestHandlingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * Reduce request handling strategy.
 *
 * @author Alioth Null
 */
@Component(CorneastOperation.REDUCE)
public class ReduceRequestHandlingStrategy implements RequestHandlingStrategy {

    @Autowired
    private ReduceDisruptor reduceDisruptor;

    @Override
    @Async("reduceExecutor")
    public CompletableFuture<ResponseProto.ResponseDTO> handle(RequestProto.RequestDTO requestDTO) {
        return reduceDisruptor.submitRequest(requestDTO);
    }

}
