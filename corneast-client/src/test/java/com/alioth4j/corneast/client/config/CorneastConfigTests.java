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

package com.alioth4j.corneast.client.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

/**
 * Test class of <code>CorneastConfig</code>
 *
 * @author Alioth Null
 */
public class CorneastConfigTests {

    @Test
    void testSetHostCorrectly() {
        CorneastConfig config = new CorneastConfig();
        config.setHost("127.0.0.1");
        Assertions.assertEquals("127.0.0.1", config.getHost());
    }

    @Test
    void testSetHostNull() {
        CorneastConfig config = new CorneastConfig();
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            config.setHost(null);
        });
    }

    @Test
    void testSetHostEmpty() {
        CorneastConfig config = new CorneastConfig();
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            config.setHost("");
        });
    }

    @Test
    void testSetPortCorrectly() {
        CorneastConfig config = new CorneastConfig();
        config.setPort(8088);
        Assertions.assertEquals(8088, config.getPort());
    }

    @Test
    void testSetNegativePort() {
        CorneastConfig config = new CorneastConfig();
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            config.setPort(-1);
        });
    }

    @Test
    void testSetTooLargePort() {
        CorneastConfig config = new CorneastConfig();
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            config.setPort(65536);
        });
    }

    @Test
    void testValidateCorrectly() {
        CorneastConfig config = new CorneastConfig();
        config.setHost("127.0.0.1");
        config.setPort(8088);
        Assertions.assertTrue(config.validate());
    }

    @Test
    void testValidateWithNullType() {
        CorneastConfig config = new CorneastConfig();
        try {
            Field hostField = config.getClass().getDeclaredField("host");
            hostField.setAccessible(true);
            hostField.set(config, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        config.setPort(8088);
        Assertions.assertFalse(config.validate());
    }

    @Test
    void testValidateWithEmptyType() {
        CorneastConfig config = new CorneastConfig();
        try {
            Field hostField = config.getClass().getDeclaredField("host");
            hostField.setAccessible(true);
            hostField.set(config, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        config.setPort(8088);
        Assertions.assertFalse(config.validate());
    }

    @Test
    void testValidateWithNegativePort() {
        CorneastConfig config = new CorneastConfig();
        config.setHost("127.0.0.1");
        try {
            Field portField = config.getClass().getDeclaredField("port");
            portField.setAccessible(true);
            portField.set(config, -1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Assertions.assertFalse(config.validate());
    }

    @Test
    void testValidateWithTooLargePort() {
        CorneastConfig config = new CorneastConfig();
        config.setHost("127.0.0.1");
        try {
            Field portField = config.getClass().getDeclaredField("port");
            portField.setAccessible(true);
            portField.set(config, 65536);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Assertions.assertFalse(config.validate());
    }

}
