package com.alioth4j.corneast_client.request;

import com.alioth4j.corneast_client.exception.RequestBuildException;
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

    private CorneastRequestBuilder() {
    }

    public static CorneastRequestBuilder newBuilder() {
        return new CorneastRequestBuilder();
    }

    public CorneastRequestBuilder setType(String type) {
        if ("register".equals(type)) {
            registerReqBuilder = RequestProto.RegisterReqDTO.newBuilder();
        } else if ("reduce".equals(type)) {
            reduceReqBuilder = RequestProto.ReduceReqDTO.newBuilder();
        } else if ("release".equals(type)) {
            releaseReqBuilder = RequestProto.ReleaseReqDTO.newBuilder();
        } else if ("query".equals(type)) {
            queryReqBuilder = RequestProto.QueryReqDTO.newBuilder();
        } else {
            throw new RequestBuildException("Request type does not exist: " + type);
        }
        protoBuilder.setType(type);
        return this;
    }

    public CorneastRequestBuilder setId(String id) {
        protoBuilder.setId(id);
        return this;
    }

    public CorneastRequestBuilder setKey(String key) {
        if (!StringUtils.hasLength(key)) {
            throw new RequestBuildException("Request key must not be null or empty.");
        }
        String type = protoBuilder.getType();
        if (!StringUtils.hasLength(type)) {
            throw new RequestBuildException("Request type has not been set.");
        }
        if ("register".equals(type)) {
            registerReqBuilder.setKey(key);
        } else if ("reduce".equals(type)) {
            reduceReqBuilder.setKey(key);
        } else if ("release".equals(type)) {
            releaseReqBuilder.setKey(key);
        } else if ("query".equals(type)) {
            queryReqBuilder.setKey(key);
        }
        return this;
    }

    public CorneastRequestBuilder setTokenCount(long tokenCount) {
        if (tokenCount < 0) {
            throw new RequestBuildException("Request tokenCount must not be less than 0.");
        }
        String type = protoBuilder.getType();
        if (!StringUtils.hasLength(type)) {
            throw new RequestBuildException("Request type has not been set to register.");
        }
        if (!"register".equals(type)) {
            throw new RequestBuildException("Only register request can set tokenCount, current request type: " + type);
        }
        registerReqBuilder.setTokenCount(tokenCount);
        hasSetTokenCount = true;
        return this;
    }

    public RequestProto.RequestDTO build() {
        String type = protoBuilder.getType();
        if (!StringUtils.hasLength(type)) {
            throw new RequestBuildException("Request type has not been set.");
        }
        if (!StringUtils.hasLength(protoBuilder.getId())) {
            throw new RequestBuildException("Request id has not been set.");
        }
        if ("register".equals(type)) {
            if (registerReqBuilder.getKey() == null) {
                throw new RequestBuildException("Request key has not been set.");
            }
            if (!hasSetTokenCount) {
                throw new RequestBuildException("Request tokenCount has not been set.");
            }
            protoBuilder.setRegisterReqDTO(registerReqBuilder.build());
        } else if ("reduce".equals(type)) {
            if (reduceReqBuilder.getKey() == null) {
                throw new RequestBuildException("Request key has not been set.");
            }
            protoBuilder.setReduceReqDTO(reduceReqBuilder.build());
        } else if ("release".equals(type)) {
            if (releaseReqBuilder.getKey() == null) {
                throw new RequestBuildException("Request key has not been set.");
            }
            protoBuilder.setReleaseReqDTO(releaseReqBuilder.build());
        } else if ("query".equals(type)) {
            if (queryReqBuilder.getKey() == null) {
                throw new RequestBuildException("Request key has not been set.");
            }
            protoBuilder.setQueryReqDTO(queryReqBuilder.build());
        }
        return protoBuilder.build();
    }

}
