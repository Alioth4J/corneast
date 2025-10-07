package com.alioth4j.corneast_client.request;

import com.alioth4j.corneast_core.proto.RequestProto;

/**
 * Construct a request object with `new` keyword.
 *
 * Usage: `new CorneastRequest(<args>).getInstance()`
 *
 * @author Alioth Null
 */
public class CorneastRequest {

    private final RequestProto.RequestDTO instance;

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
    public CorneastRequest(String type, String id, String key, Long tokenCount) {
        this.instance = CorneastRequestBuilder.newBuilder()
                .setType(type)
                .setId(id)
                .setKey(key)
                .setTokenCount(tokenCount)
                .build();
    }

    public RequestProto.RequestDTO getInstance() {
        return this.instance;
    }

}
