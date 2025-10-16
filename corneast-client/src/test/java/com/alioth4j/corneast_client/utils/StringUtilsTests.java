package com.alioth4j.corneast_client.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test class for <code>StringUtils</code>.
 *
 * @author Alioth Null
 */
public class StringUtilsTests {

    @Test
    void testNullStr() {
        Assertions.assertFalse(StringUtils.hasLength(null));
    }

    @Test
    void testEmptyStr() {
        Assertions.assertFalse(StringUtils.hasLength(""));
    }

    @Test
    void testStrWithLength() {
        Assertions.assertTrue(StringUtils.hasLength("text"));
    }

}
