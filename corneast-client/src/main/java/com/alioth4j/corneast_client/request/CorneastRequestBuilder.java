package com.alioth4j.corneast_client.request;

import com.alioth4j.corneast_client.exception.RequestBuildException;
import com.alioth4j.corneast_core.common.CorneastOperation;
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

    private boolean hasTypeSet = false;
    private boolean hasIdSet = false;
    private boolean hasKeySet = false;
    private boolean hasTokenCountSet = false;

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
    private static final String UNREACHABLE_MSG = "Reach unreachable code.";

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
        // Currently id must not be null or empty. This will be changed.
        // See `build()`.
        if (!StringUtils.hasLength(id)) {
            throw new RequestBuildException(ID_NULL_MSG);
        }
        protoBuilder.setId(id);
        hasIdSet = true;
        return this;
    }

    public CorneastRequestBuilder setKey(String key) {
        if (!StringUtils.hasLength(key)) {
            throw new RequestBuildException(KEY_NULL_MSG);
        }
        String type = protoBuilder.getType();
        if (!StringUtils.hasLength(type)) {
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
        if (!StringUtils.hasLength(type)) {
            throw new RequestBuildException(TYPE_OF_REGISTER_NOT_SET_BEFORE_TOKENCOUNT_SET_MSG);
        }
        if (!CorneastOperation.REGISTER.equals(type)) {
            throw new RequestBuildException(TOKENCOUNT_FOR_REGISTER_ONLY_MSG + type);
        }
        registerReqBuilder.setTokenCount(tokenCount);
        hasTokenCountSet = true;
        return this;
    }

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

    private void checkHasTokenCountSet() {
        if (!hasTokenCountSet) {
            throw new RequestBuildException(TOKENCOUNT_NOT_SET_MSG);
        }
    }

    private void checkBasicFields() {
        if (!hasTypeSet) {
            throw new RequestBuildException(TYPE_NOT_SET_MSG);
        }
        if (!hasIdSet) {
            throw new RequestBuildException(ID_NOT_SET_MSG);
        }
        if (!hasKeySet) {
            throw new RequestBuildException(KEY_NOT_SET_MSG);
        }
    }

}
