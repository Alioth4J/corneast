/*
 * Corneast
 * Copyright (C) 2026 Alioth Null
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

package com.alioth4j.corneast.core.ringbuffer;

import com.alioth4j.corneast.common.proto.ResponseProto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;

class ReduceEventTests {

    @Test
    void testNoArgsConstructor() {
        ReduceEvent reduceEvent = new ReduceEvent();
        Assertions.assertNotNull(reduceEvent);
        Assertions.assertNull(reduceEvent.getId());
        Assertions.assertNull(reduceEvent.getKey());
        Assertions.assertNull(reduceEvent.getFuture());
    }

    @Test
    void testSetAndGetId() {
        ReduceEvent reduceEvent = new ReduceEvent();
        reduceEvent.setId("id");
        Assertions.assertEquals("id", reduceEvent.getId());
    }

    @Test
    void testNullId() {
        ReduceEvent reduceEvent = new ReduceEvent();
        reduceEvent.setId(null);
        // allowed
        Assertions.assertNull(reduceEvent.getId());
    }

    @Test
    void testEmptyId() {
        ReduceEvent reduceEvent = new ReduceEvent();
        reduceEvent.setId("");
        // allowed
        Assertions.assertEquals("", reduceEvent.getId());
    }

    @Test
    void testSetAndGetKey() {
        ReduceEvent reduceEvent = new ReduceEvent();
        reduceEvent.setKey("register");
        Assertions.assertEquals("register", reduceEvent.getKey());
    }

    @Test
    void testNullKey() {
        ReduceEvent reduceEvent = new ReduceEvent();
        // not allowed
        Assertions.assertThrows(IllegalStateException.class, () -> {
            reduceEvent.setKey(null);
        });
    }

    @Test
    void testEmptyKey() {
        ReduceEvent reduceEvent = new ReduceEvent();
        reduceEvent.setKey("");
        // allowed
        Assertions.assertEquals("", reduceEvent.getKey());
    }

    @Test
    void testSetAndGetFuture() {
        ReduceEvent reduceEvent = new ReduceEvent();
        CompletableFuture<ResponseProto.ResponseDTO> future = new CompletableFuture<>();
        reduceEvent.setFuture(future);
        Assertions.assertSame(future, reduceEvent.getFuture());
    }

    @Test
    void testToStringContainsId() {
        ReduceEvent reduceEvent = new ReduceEvent();
        String id = "test123";
        reduceEvent.setId(id);
        Assertions.assertTrue(reduceEvent.toString().contains(id));
    }

    @Test
    void testToStringContainsKey() {
        ReduceEvent reduceEvent = new ReduceEvent();
        String key = "biz456";
        reduceEvent.setKey(key);
        Assertions.assertTrue(reduceEvent.toString().contains(key));
    }

    @Test
    void testToStringContainsFuture() {
        ReduceEvent reduceEvent = new ReduceEvent();
        CompletableFuture<ResponseProto.ResponseDTO> future = new CompletableFuture<>();
        reduceEvent.setFuture(future);
        Assertions.assertTrue(reduceEvent.toString().contains(future.toString()));
    }

}
