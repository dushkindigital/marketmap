package com.libereco.core.exceptions;

public class GenericLiberecoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public GenericLiberecoException(String message) {
        super(message);
    }

    public GenericLiberecoException(String message, Throwable e) {
        super(message, e);
    }
}
