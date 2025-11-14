package com.alioth4j.corneast_core.exception;

/**
 * Simple implementation of handling exception, extending `RuntimeException`.
 *
 * @author Alioth Null
 */
public class CorneastHandleException extends RuntimeException {

    public CorneastHandleException() {
        super();
    }

    public CorneastHandleException(String msg) {
        super(msg);
    }

    public CorneastHandleException(Throwable cause) {
        super(cause);
    }

    public CorneastHandleException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
