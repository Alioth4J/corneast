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

package com.alioth4j.corneast_common.operation;

/**
 * Constants of operation types.
 *
 * - register
 * - reduce
 * - release
 * - query
 *
 * - idempotent
 * - unknown
 *
 * @author Alioth Null
 */
public class CorneastOperation {

    public static final String REGISTER = "register";
    public static final String REDUCE = "reduce";
    public static final String RELEASE = "release";
    public static final String QUERY = "query";

    // ======================
    // only used in response
    public static final String IDEMPOTENT = "idempotent";
    public static final String UNKNOWN = "unknown";
    public static final String RATE_LIMITED = "rateLimited";


    /**
     * Private constructor to prevent instantiation.
     */
    private CorneastOperation() {
    }

}
