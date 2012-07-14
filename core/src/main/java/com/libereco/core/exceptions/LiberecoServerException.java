package com.libereco.core.exceptions;

public class LiberecoServerException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public LiberecoServerException(String message) {
        super(message);
    }

    public LiberecoServerException(String message, Throwable e) {
        super(message, e);
    }
}
