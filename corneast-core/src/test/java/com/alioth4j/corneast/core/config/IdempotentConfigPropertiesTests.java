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

package com.alioth4j.corneast.core.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class IdempotentConfigPropertiesTests {

    @Test
    void testNoArgsConstructor() {
        IdempotentConfigProperties props = new IdempotentConfigProperties();
        Assertions.assertNotNull(props);
        Assertions.assertNull(props.getRedisEndpoints());
        Assertions.assertEquals(0, props.getRetryAttempts());
        Assertions.assertEquals(0, props.getRetryInterval());
        Assertions.assertEquals(0, props.getMasterConnectionPoolSize());
        Assertions.assertEquals(0, props.getSlaveConnectionPoolSize());
    }

    @Test
    void testConstructorWithRedisEndpoints() {
        List<String> endpoints = List.of("redis://127.0.0.1:6000", "redis://127.0.0.1:6001");
        IdempotentConfigProperties props = new IdempotentConfigProperties(endpoints);
        Assertions.assertEquals(endpoints, props.getRedisEndpoints());
    }

    @Test
    void testSetAndGetRedisEndpoints() {
        IdempotentConfigProperties props = new IdempotentConfigProperties();
        List<String> endpoints = List.of("redis://127.0.0.1:6000");
        props.setRedisEndpoints(endpoints);
        Assertions.assertEquals(endpoints, props.getRedisEndpoints());
    }

    @Test
    void testSetAndGetRetryAttempts() {
        IdempotentConfigProperties props = new IdempotentConfigProperties();
        props.setRetryAttempts(10);
        Assertions.assertEquals(10, props.getRetryAttempts());
    }

    @Test
    void testSetAndGetRetryInterval() {
        IdempotentConfigProperties props = new IdempotentConfigProperties();
        props.setRetryInterval(1000);
        Assertions.assertEquals(1000, props.getRetryInterval());
    }

    @Test
    void testSetAndGetMasterConnectionPoolSize() {
        IdempotentConfigProperties props = new IdempotentConfigProperties();
        props.setMasterConnectionPoolSize(2000);
        Assertions.assertEquals(2000, props.getMasterConnectionPoolSize());
    }

    @Test
    void testSetAndGetSlaveConnectionPoolSize() {
        IdempotentConfigProperties props = new IdempotentConfigProperties();
        props.setSlaveConnectionPoolSize(1000);
        Assertions.assertEquals(1000, props.getSlaveConnectionPoolSize());
    }

}
