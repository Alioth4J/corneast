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

import com.alioth4j.corneast_core.proto.RequestProto;

/**
 * Construct a request object with `new` keyword.
 *
 * Usages:
 * - `new CorneastRequest(<args>).instance`
 * - `new CorneastRequest(<args>).get()`
 * - `new CorneastRequest(<args>).getInstance()`
 *
 * @author Alioth Null
 */
public class CorneastRequest {

    // The real instance this class constructs.
    public final RequestProto.RequestDTO instance;

    /**
     * Constructor for requests of types excepts register.
     * @param type request type
     * @param id request id
     * @param key request key
     */
    public CorneastRequest(String type, String id, String key) {
        this.instance = CorneastRequestBuilder.newBuilder()
                .setType(type)
                .setId(id)
                .setKey(key)
                .build();
    }

    /**
     * For register requests only.
     */
    public CorneastRequest(String type, String id, String key, long tokenCount) {
        this.instance = CorneastRequestBuilder.newBuilder()
                .setType(type)
                .setId(id)
                .setKey(key)
                .setTokenCount(tokenCount)
                .build();
    }

    /**
     * Constructor for requests of types excepts register, disabling idempotence.
     * @param type request type
     * @param key request key
     */
    public CorneastRequest(String type, String key) {
        this(type, "", key);
    }

    /**
     * Constructor for register requests only, disabling idempotence.
     * @param type
     * @param key
     * @param tokenCount
     */
    public CorneastRequest(String type, String key, long tokenCount) {
        this(type, "", key, tokenCount);
    }

    /**
     * Get the constructed request instance.
     * @return constructed request instance
     */
    public RequestProto.RequestDTO getInstance() {
        return this.instance;
    }

    /**
     * Get the constructed request instance.
     * This method is the alias of `getInstance()`.
     * @return constructed request instance
     */
    public RequestProto.RequestDTO get() {
        return getInstance();
    }

}
