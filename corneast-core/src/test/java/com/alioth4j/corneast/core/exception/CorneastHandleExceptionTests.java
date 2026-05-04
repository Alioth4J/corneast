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

package com.alioth4j.corneast.core.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CorneastHandleExceptionTests {

    @Test
    void testNoArgsConstructor() {
        CorneastHandleException ex = new CorneastHandleException();
        Assertions.assertNotNull(ex);
        Assertions.assertNull(ex.getMessage());
        Assertions.assertNull(ex.getCause());
    }

    @Test
    void testConstructorWithMessage() {
        String msg = "test error message";
        CorneastHandleException ex = new CorneastHandleException(msg);
        Assertions.assertEquals(msg, ex.getMessage());
        Assertions.assertNull(ex.getCause());
    }

    @Test
    void testConstructorWithCause() {
        Throwable cause = new IllegalArgumentException("root cause");
        CorneastHandleException ex = new CorneastHandleException(cause);
        Assertions.assertEquals(cause, ex.getCause());
        Assertions.assertNotNull(ex.getMessage());
    }

    @Test
    void testConstructorWithMessageAndCause() {
        String msg = "wrapped error";
        Throwable cause = new IllegalStateException("underlying");
        CorneastHandleException ex = new CorneastHandleException(msg, cause);
        Assertions.assertEquals(msg, ex.getMessage());
        Assertions.assertEquals(cause, ex.getCause());
    }

    @Test
    void testIsRuntimeException() {
        CorneastHandleException ex = new CorneastHandleException();
        Assertions.assertInstanceOf(RuntimeException.class, ex);
    }

}
