package com.libereco.core.exceptions;

public class ExternalServiceException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private static final String MESSAGE = "No authorization found for marketplace %s. Please first authenticate yourself with %s";

    public ExternalServiceException(String marketplace) {
        super(String.format(MESSAGE, marketplace));
    }

    public ExternalServiceException(String message, Throwable e) {
        super(message, e);
    }

}
