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

package com.alioth4j.corneast_client.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test class for <code>StringUtil</code>.
 *
 * @author Alioth Null
 */
public class StringUtilTests {

    @Test
    void testNullStr() {
        Assertions.assertFalse(StringUtil.hasLength(null));
    }

    @Test
    void testEmptyStr() {
        Assertions.assertFalse(StringUtil.hasLength(""));
    }

    @Test
    void testStrWithLength() {
        Assertions.assertTrue(StringUtil.hasLength("text"));
    }

}
