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

package com.alioth4j.corneast.client.request;

import com.alioth4j.corneast.client.exception.RequestBuildException;
import com.alioth4j.corneast.client.util.StringUtil;
import com.alioth4j.corneast.common.operation.CorneastOperation;
import com.alioth4j.corneast.common.proto.RequestProto;

/**
 * Protobuf request builder for client to use.
 *
 * @author Alioth Null
 */
public class CorneastRequestBuilder {

    // Outer builder object of the request.
    private RequestProto.RequestDTO.Builder protoBuilder = RequestProto.RequestDTO.newBuilder();

    // Inner builder object of the request.
    // Choose one of the following.
    private RequestProto.RegisterReqDTO.Builder registerReqBuilder;
    private RequestProto.ReduceReqDTO.Builder reduceReqBuilder;
    private RequestProto.ReleaseReqDTO.Builder releaseReqBuilder;
    private RequestProto.QueryReqDTO.Builder queryReqBuilder;

    // Record whether a filed is correctly set.
    private boolean hasTypeSet = false;
    private boolean hasIdSet = false;
    private boolean hasKeySet = false;
    private boolean hasTokenCountSet = false;

    // Message Strings.
    private static final String TYPE_NULL_MSG = "Request type must not be null or empty.";
    private static final String TYPE_NOT_EXISTS_MSG = "Request type does not exist: ";
    private static final String TYPE_NOT_SET_MSG = "Request type has not been set.";
    private static final String TYPE_OF_REGISTER_NOT_SET_BEFORE_TOKENCOUNT_SET_MSG = "Request type has not been set to register.";
    private static final String KEY_NULL_MSG = "Request key must not be null or empty.";
    private static final String KEY_NOT_SET_MSG = "Request key has not been set.";
    private static final String TOKENCOUNT_NOT_SET_MSG = "Request tokenCount has not been set.";
    private static final String TOKENCOUNT_LESS_THAN_ZERO_MSG = "Request tokenCount must not be less than 0.";
    private static final String TOKENCOUNT_FOR_REGISTER_ONLY_MSG = "Only register request can set tokenCount, current request type: ";
    private static final String UNREACHABLE_MSG = "Reach unreachable code.";

    /**
     * Private constructor, invoked by `newBuilder()`.
     */
    private CorneastRequestBuilder() {
    }

    /**
     * Construct the builder instance.
     * @return instance of `CorneastRequestBuilder`
     */
    public static CorneastRequestBuilder newBuilder() {
        return new CorneastRequestBuilder();
    }

    public CorneastRequestBuilder setType(String type) {
        if (!StringUtil.hasLength(type)) {
            throw new RequestBuildException(TYPE_NULL_MSG);
        }
        switch (type) {
            case CorneastOperation.REGISTER: {
                protoBuilder.setType(CorneastOperation.REGISTER);
                registerReqBuilder = RequestProto.RegisterReqDTO.newBuilder();
                break;
            }
            case CorneastOperation.REDUCE: {
                protoBuilder.setType(CorneastOperation.REDUCE);
                reduceReqBuilder = RequestProto.ReduceReqDTO.newBuilder();
                break;
            }
            case CorneastOperation.RELEASE: {
                protoBuilder.setType(CorneastOperation.RELEASE);
                releaseReqBuilder = RequestProto.ReleaseReqDTO.newBuilder();
                break;
            }
            case CorneastOperation.QUERY: {
                protoBuilder.setType(CorneastOperation.QUERY);
                queryReqBuilder = RequestProto.QueryReqDTO.newBuilder();
                break;
            }
            default: {
                throw new RequestBuildException(TYPE_NOT_EXISTS_MSG + type);
            }
        }
        hasTypeSet = true;
        return this;
    }

    public CorneastRequestBuilder setId(String id) {
        if (id == null) {
            protoBuilder.setId("");
        } else {
            protoBuilder.setId(id);
        }
        hasIdSet = true;
        return this;
    }

    public CorneastRequestBuilder setKey(String key) {
        if (!StringUtil.hasLength(key)) {
            throw new RequestBuildException(KEY_NULL_MSG);
        }
        String type = protoBuilder.getType();
        if (!StringUtil.hasLength(type)) {
            throw new RequestBuildException(TYPE_NOT_SET_MSG);
        }
        switch (type) {
            case CorneastOperation.REGISTER: {
                registerReqBuilder.setKey(key);
                break;
            }
            case CorneastOperation.REDUCE: {
                reduceReqBuilder.setKey(key);
                break;
            }
            case CorneastOperation.RELEASE: {
                releaseReqBuilder.setKey(key);
                break;
            }
            case CorneastOperation.QUERY: {
                queryReqBuilder.setKey(key);
                break;
            }
            default: {
                throw new RequestBuildException(TYPE_NOT_EXISTS_MSG + type);
            }
        }
        hasKeySet = true;
        return this;
    }

    public CorneastRequestBuilder setTokenCount(long tokenCount) {
        if (tokenCount < 0) {
            throw new RequestBuildException(TOKENCOUNT_LESS_THAN_ZERO_MSG);
        }
        String type = protoBuilder.getType();
        if (!StringUtil.hasLength(type)) {
            throw new RequestBuildException(TYPE_OF_REGISTER_NOT_SET_BEFORE_TOKENCOUNT_SET_MSG);
        }
        if (!CorneastOperation.REGISTER.equals(type)) {
            throw new RequestBuildException(TOKENCOUNT_FOR_REGISTER_ONLY_MSG + type);
        }
        registerReqBuilder.setTokenCount(tokenCount);
        hasTokenCountSet = true;
        return this;
    }

    /**
     * Build the complete request.
     * @return proto request object
     */
    public RequestProto.RequestDTO build() {
        checkBasicFields();

        switch (protoBuilder.getType()) {
            case CorneastOperation.REGISTER: {
                checkHasTokenCountSet();
                protoBuilder.setRegisterReqDTO(registerReqBuilder.build());
                break;
            }
            case CorneastOperation.REDUCE: {
                protoBuilder.setReduceReqDTO(reduceReqBuilder.build());
                break;
            }
            case CorneastOperation.RELEASE: {
                protoBuilder.setReleaseReqDTO(releaseReqBuilder.build());
                break;
            }
            case CorneastOperation.QUERY: {
                protoBuilder.setQueryReqDTO(queryReqBuilder.build());
                break;
            }
            default: {
                throw new RequestBuildException(UNREACHABLE_MSG);
            }
        }
        return protoBuilder.build();
    }

    /**
     * TokenCount check for register request in `build()`.
     */
    private void checkHasTokenCountSet() {
        if (!hasTokenCountSet) {
            throw new RequestBuildException(TOKENCOUNT_NOT_SET_MSG);
        }
    }

    /**
     * Fields setting check in `build()`.
     */
    private void checkBasicFields() {
        if (!hasTypeSet) {
            throw new RequestBuildException(TYPE_NOT_SET_MSG);
        }
        if (!hasIdSet) {
            protoBuilder.setId("");
        }
        if (!hasKeySet) {
            throw new RequestBuildException(KEY_NOT_SET_MSG);
        }
    }

}
