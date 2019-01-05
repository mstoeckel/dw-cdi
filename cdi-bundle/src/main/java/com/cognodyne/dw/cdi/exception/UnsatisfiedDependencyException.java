package com.cognodyne.dw.cdi.exception;

public class UnsatisfiedDependencyException extends RuntimeException {
    private static final long serialVersionUID = 5775069784236286403L;

    public UnsatisfiedDependencyException() {
        super();
    }

    public UnsatisfiedDependencyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public UnsatisfiedDependencyException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsatisfiedDependencyException(String message) {
        super(message);
    }

    public UnsatisfiedDependencyException(Throwable cause) {
        super(cause);
    }
}
