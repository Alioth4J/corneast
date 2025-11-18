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

package com.alioth4j.corneast_core.ringbuffer;

import com.alioth4j.corneast_core.proto.ResponseProto;

import java.util.concurrent.CompletableFuture;

/**
 * Used in RingBuffer.
 *
 * Stores the key and the CompletableFuture for the response.
 *
 * @author Alioth Null
 */
public class ReduceEvent {

    private String id;

    private String key;

    // for response
    private CompletableFuture<ResponseProto.ResponseDTO> future;


    public ReduceEvent() {
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public CompletableFuture<ResponseProto.ResponseDTO> getFuture() {
        return future;
    }

    public void setFuture(CompletableFuture<ResponseProto.ResponseDTO> future) {
        this.future = future;
    }

    @Override
    public String toString() {
        return "ReduceEvent{" +
                "key='" + key + '\'' +
                ", future=" + future +
                '}';
    }

}
