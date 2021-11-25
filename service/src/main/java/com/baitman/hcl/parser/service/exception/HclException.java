package com.baitman.hcl.parser.service.exception;

public class HclException extends RuntimeException {
    public HclException(String message) {
        super(message);
    }

    public HclException(String message, Throwable cause) {
        super(message, cause);
    }

    public HclException(Throwable cause) {
        this(cause.getMessage(), cause);
    }
}
