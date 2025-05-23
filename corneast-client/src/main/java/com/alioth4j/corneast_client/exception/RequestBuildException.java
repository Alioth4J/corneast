package com.alioth4j.corneast_client.exception;

/**
 * Exception that may be thrown when building a protobuf request.
 * This is a RuntimeException.
 *
 * @author Alioth Null
 */
public class RequestBuildException extends RuntimeException {

    public RequestBuildException(String message) {
        super(message);
    }

}
