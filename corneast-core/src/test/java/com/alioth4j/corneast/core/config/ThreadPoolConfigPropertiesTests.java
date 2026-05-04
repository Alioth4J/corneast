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

import java.util.concurrent.TimeUnit;

class ThreadPoolConfigPropertiesTests {

    @Test
    void testNoArgsConstructor() {
        ThreadPoolConfigProperties props = new ThreadPoolConfigProperties();
        Assertions.assertNull(props.getUnified());
    }

    @Test
    void testSetAndGetUnified() {
        ThreadPoolConfigProperties props = new ThreadPoolConfigProperties();
        ThreadPoolConfigProperties.SingleThreadPoolConfigProperties unified =
                new ThreadPoolConfigProperties.SingleThreadPoolConfigProperties();
        props.setUnified(unified);
        Assertions.assertSame(unified, props.getUnified());
    }

    @Test
    void testSingleThreadPoolConfigPropertiesDefaults() {
        ThreadPoolConfigProperties.SingleThreadPoolConfigProperties single =
                new ThreadPoolConfigProperties.SingleThreadPoolConfigProperties();
        Assertions.assertEquals(0, single.getCorePoolSize());
        Assertions.assertEquals(0, single.getMaximumPoolSize());
        Assertions.assertEquals(0L, single.getKeepAliveTime());
        Assertions.assertNull(single.getUnit());
    }

    @Test
    void testSingleThreadPoolSetAndGetCorePoolSize() {
        ThreadPoolConfigProperties.SingleThreadPoolConfigProperties single =
                new ThreadPoolConfigProperties.SingleThreadPoolConfigProperties();
        single.setCorePoolSize(10);
        Assertions.assertEquals(10, single.getCorePoolSize());
    }

    @Test
    void testSingleThreadPoolSetAndGetMaximumPoolSize() {
        ThreadPoolConfigProperties.SingleThreadPoolConfigProperties single =
                new ThreadPoolConfigProperties.SingleThreadPoolConfigProperties();
        single.setMaximumPoolSize(20);
        Assertions.assertEquals(20, single.getMaximumPoolSize());
    }

    @Test
    void testSingleThreadPoolSetAndGetKeepAliveTime() {
        ThreadPoolConfigProperties.SingleThreadPoolConfigProperties single =
                new ThreadPoolConfigProperties.SingleThreadPoolConfigProperties();
        single.setKeepAliveTime(60L);
        Assertions.assertEquals(60L, single.getKeepAliveTime());
    }

    @Test
    void testSingleThreadPoolSetAndGetUnit() {
        ThreadPoolConfigProperties.SingleThreadPoolConfigProperties single =
                new ThreadPoolConfigProperties.SingleThreadPoolConfigProperties();
        single.setUnit(TimeUnit.MINUTES);
        Assertions.assertEquals(TimeUnit.MINUTES, single.getUnit());
    }

}
