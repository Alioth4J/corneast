package com.alioth4j.corneast_client.request;

import com.alioth4j.corneast_client.exception.RequestBuildException;
import com.alioth4j.corneast_core.common.Operation;
import com.alioth4j.corneast_core.proto.RequestProto;
import org.springframework.util.StringUtils;

/**
 * Protobuf request builder for client to use.
 *
 * @author Alioth Null
 */
public class CorneastRequestBuilder {

    private RequestProto.RequestDTO.Builder protoBuilder = RequestProto.RequestDTO.newBuilder();

    // Choose one of the following four.
    private RequestProto.RegisterReqDTO.Builder registerReqBuilder;
    private RequestProto.ReduceReqDTO.Builder reduceReqBuilder;
    private RequestProto.ReleaseReqDTO.Builder releaseReqBuilder;
    private RequestProto.QueryReqDTO.Builder queryReqBuilder;

    // for register request only
    private boolean hasSetTokenCount = false;

    private static final String TYPE_NULL_MSG = "Request type must not be null or empty.";
    private static final String TYPE_NOT_EXISTS_MSG = "Request type does not exist: ";
    private static final String TYPE_NOT_SET_MSG = "Request type has not been set.";
    private static final String TYPE_OF_REGISTER_NOT_SET_BEFORE_TOKENCOUNT_SET_MSG = "Request type has not been set to register.";
    private static final String KEY_NULL_MSG = "Request key must not be null or empty.";
    private static final String KEY_NOT_SET_MSG = "Request key has not been set.";
    private static final String ID_NULL_MSG = "Id must not be null or empty.";
    private static final String ID_NOT_SET_MSG = "Request id has not been set.";
    private static final String TOKENCOUNT_NOT_SET_MSG = "Request tokenCount has not been set.";
    private static final String TOKENCOUNT_LESS_THAN_ZERO_MSG = "Request tokenCount must not be less than 0.";
    private static final String TOKENCOUNT_FOR_REGISTER_ONLY_MSG = "Only register request can set tokenCount, current request type: ";

    private CorneastRequestBuilder() {
    }

    public static CorneastRequestBuilder newBuilder() {
        return new CorneastRequestBuilder();
    }

    public CorneastRequestBuilder setType(String type) {
        if (!StringUtils.hasLength(type)) {
            throw new RequestBuildException(TYPE_NULL_MSG);
        }
        switch (type) {
            case Operation.REGISTER: {
                registerReqBuilder = RequestProto.RegisterReqDTO.newBuilder();
                break;
            }
            case Operation.REDUCE: {
                reduceReqBuilder = RequestProto.ReduceReqDTO.newBuilder();
                break;
            }
            case Operation.RELEASE: {
                releaseReqBuilder = RequestProto.ReleaseReqDTO.newBuilder();
                break;
            }
            case Operation.QUERY: {
                queryReqBuilder = RequestProto.QueryReqDTO.newBuilder();
                break;
            }
            default: {
                throw new RequestBuildException(TYPE_NOT_EXISTS_MSG + type);
            }
        }
        return this;
    }

    public CorneastRequestBuilder setId(String id) {
        // Currently id must not be null or empty. This will be changed.
        // See `build()`.
        if (!StringUtils.hasLength(id)) {
            throw new RequestBuildException(ID_NULL_MSG);
        }
        protoBuilder.setId(id);
        return this;
    }

    public CorneastRequestBuilder setKey(String key) {
        if (!StringUtils.hasLength(key)) {
            throw new RequestBuildException(KEY_NULL_MSG);
        }
        String type = protoBuilder.getType();
        if (!StringUtils.hasLength(type)) {
            throw new RequestBuildException(KEY_NOT_SET_MSG);
        }
        switch (type) {
            case Operation.REGISTER: {
                registerReqBuilder.setKey(key);
                break;
            }
            case Operation.REDUCE: {
                reduceReqBuilder.setKey(key);
                break;
            }
            case Operation.RELEASE: {
                releaseReqBuilder.setKey(key);
                break;
            }
            case Operation.QUERY: {
                queryReqBuilder.setKey(key);
                break;
            }
            default: {
                throw new RequestBuildException(TYPE_NOT_EXISTS_MSG + type);
            }
        }
        return this;
    }

    public CorneastRequestBuilder setTokenCount(long tokenCount) {
        if (tokenCount < 0) {
            throw new RequestBuildException(TOKENCOUNT_LESS_THAN_ZERO_MSG);
        }
        String type = protoBuilder.getType();
        if (!StringUtils.hasLength(type)) {
            throw new RequestBuildException(TYPE_OF_REGISTER_NOT_SET_BEFORE_TOKENCOUNT_SET_MSG);
        }
        if (!Operation.REGISTER.equals(type)) {
            throw new RequestBuildException(TOKENCOUNT_FOR_REGISTER_ONLY_MSG + type);
        }
        registerReqBuilder.setTokenCount(tokenCount);
        hasSetTokenCount = true;
        return this;
    }

    public RequestProto.RequestDTO build() {
        String type = protoBuilder.getType();
        if (!StringUtils.hasLength(type)) {
            throw new RequestBuildException(TYPE_NOT_SET_MSG);
        }
        String id = protoBuilder.getId();
        if (!StringUtils.hasLength(id)) {
            throw new RequestBuildException(ID_NOT_SET_MSG);
        }
        switch (type) {
            case Operation.REGISTER: {
                if (registerReqBuilder.getKey() == null) {
                    throw new RequestBuildException(KEY_NOT_SET_MSG);
                }
                if (!hasSetTokenCount) {
                    throw new RequestBuildException(TOKENCOUNT_NOT_SET_MSG);
                }
                protoBuilder.setRegisterReqDTO(registerReqBuilder.build());
                break;
            }
            case Operation.REDUCE: {
                if (reduceReqBuilder.getKey() == null) {
                    throw new RequestBuildException(KEY_NOT_SET_MSG);
                }
                protoBuilder.setReduceReqDTO(reduceReqBuilder.build());
                break;
            }
            case Operation.RELEASE: {
                if (releaseReqBuilder.getKey() == null) {
                    throw new RequestBuildException(KEY_NOT_SET_MSG);
                }
                protoBuilder.setReleaseReqDTO(releaseReqBuilder.build());
                break;
            }
            case Operation.QUERY: {
                if (queryReqBuilder.getKey() == null) {
                    throw new RequestBuildException(KEY_NOT_SET_MSG);
                }
                protoBuilder.setQueryReqDTO(queryReqBuilder.build());
                break;
            }
            default: {
                throw new RequestBuildException(TYPE_NOT_SET_MSG + type);
            }
        }
        return protoBuilder.build();
    }

}
