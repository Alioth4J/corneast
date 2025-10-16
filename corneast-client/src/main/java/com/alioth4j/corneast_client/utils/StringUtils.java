package com.alioth4j.corneast_client.utils;

/**
 * Util class for String operations.
 *
 * @author Alioth Null
 */
public class StringUtils {

    /**
     * Check whether a String has length.
     * @param str the String to be checked
     * @return null || "" -> false; others -> true;
     */
    public static boolean hasLength(String str) {
        return str != null && !str.isEmpty();
    }

}