package com.almoxaf.api.exception;

public abstract class ApiException extends RuntimeException {

    private final int httpStatus;

    protected ApiException(String message, int httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public int getHttpStatus() {
        return httpStatus;
    }
}
