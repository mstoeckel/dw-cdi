package com.cognodyne.dw.cdi.exception;

public class InvalidConfigurationException extends Exception {
    private static final long serialVersionUID = -2335261703275478336L;

    public InvalidConfigurationException() {
        super();
    }

    public InvalidConfigurationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public InvalidConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidConfigurationException(String message) {
        super(message);
    }

    public InvalidConfigurationException(Throwable cause) {
        super(cause);
    }
}
