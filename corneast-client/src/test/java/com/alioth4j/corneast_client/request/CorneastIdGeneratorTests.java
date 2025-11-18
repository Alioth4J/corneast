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

package com.alioth4j.corneast_client.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.regex.Pattern;

/**
 * Test class for `CorneastIdGenerator`.
 *
 * @author Alioth Null
 */
public class CorneastIdGeneratorTests {

    private static final Pattern UUID_PATTERN = Pattern.compile(
            "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$",
            Pattern.CASE_INSENSITIVE
    );

    @Test
    void testUUIDValidFormat() {
        String uuid = CorneastIdGenerator.uuid();
        Assertions.assertNotNull(uuid);
        Assertions.assertTrue(UUID_PATTERN.matcher(uuid).matches());
    }

    @Test
    void testUUIDUnique() {
        HashSet<String> set = new HashSet<>();
        for (int i = 0; i < 10000; i++) {
            String uuid = CorneastIdGenerator.uuid();
            Assertions.assertTrue(set.add(uuid));
        }
    }

    @Test
    void testSnowflakeUnique() {
        HashSet<String> set = new HashSet<>();
        CorneastIdGenerator.initSnowflake(0, 0);
        for (int i = 0; i < 10000; i++) {
            String snowflake = CorneastIdGenerator.snowflake();
            Assertions.assertTrue(set.add(snowflake));
        }
    }

    @Test
    void testNotInitSnowflake() {
        Assertions.assertThrows(IllegalStateException.class, () -> {
            String snowflake = CorneastIdGenerator.snowflake();
        });
    }

}
