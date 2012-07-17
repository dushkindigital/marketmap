package com.libereco.core.exceptions;

public class LiberecoResourceNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public LiberecoResourceNotFoundException(String message) {
        super(message);
    }

}
