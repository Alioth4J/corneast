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

class StorageConfigPropertiesTests {

    @Test
    void testNoArgsConstructor() {
        StorageConfigProperties props = new StorageConfigProperties();
        Assertions.assertNull(props.getMasters());
        Assertions.assertNull(props.getSentinels());
        Assertions.assertEquals(0, props.getDatabase());
        Assertions.assertEquals(0, props.getRetryAttempts());
        Assertions.assertEquals(0, props.getRetryInterval());
        Assertions.assertEquals(0, props.getTimeout());
        Assertions.assertEquals(0, props.getConnectTimeout());
        Assertions.assertEquals(0, props.getMasterConnectionPoolSize());
        Assertions.assertEquals(0, props.getSlaveConnectionPoolSize());
    }

    @Test
    void testSetAndGetMasters() {
        StorageConfigProperties props = new StorageConfigProperties();
        List<String> masters = List.of("master-1", "master-2");
        props.setMasters(masters);
        Assertions.assertEquals(masters, props.getMasters());
    }

    @Test
    void testSetAndGetSentinels() {
        StorageConfigProperties props = new StorageConfigProperties();
        List<String> sentinels = List.of("redis://172.19.0.10:26379", "redis://172.19.0.11:26379");
        props.setSentinels(sentinels);
        Assertions.assertEquals(sentinels, props.getSentinels());
    }

    @Test
    void testSetAndGetDatabase() {
        StorageConfigProperties props = new StorageConfigProperties();
        props.setDatabase(1);
        Assertions.assertEquals(1, props.getDatabase());
    }

    @Test
    void testSetAndGetRetryAttempts() {
        StorageConfigProperties props = new StorageConfigProperties();
        props.setRetryAttempts(3);
        Assertions.assertEquals(3, props.getRetryAttempts());
    }

    @Test
    void testSetAndGetRetryInterval() {
        StorageConfigProperties props = new StorageConfigProperties();
        props.setRetryInterval(500);
        Assertions.assertEquals(500, props.getRetryInterval());
    }

    @Test
    void testSetAndGetTimeout() {
        StorageConfigProperties props = new StorageConfigProperties();
        props.setTimeout(3000);
        Assertions.assertEquals(3000, props.getTimeout());
    }

    @Test
    void testSetAndGetConnectTimeout() {
        StorageConfigProperties props = new StorageConfigProperties();
        props.setConnectTimeout(200);
        Assertions.assertEquals(200, props.getConnectTimeout());
    }

    @Test
    void testSetAndGetMasterConnectionPoolSize() {
        StorageConfigProperties props = new StorageConfigProperties();
        props.setMasterConnectionPoolSize(200);
        Assertions.assertEquals(200, props.getMasterConnectionPoolSize());
    }

    @Test
    void testSetAndGetSlaveConnectionPoolSize() {
        StorageConfigProperties props = new StorageConfigProperties();
        props.setSlaveConnectionPoolSize(100);
        Assertions.assertEquals(100, props.getSlaveConnectionPoolSize());
    }

}
