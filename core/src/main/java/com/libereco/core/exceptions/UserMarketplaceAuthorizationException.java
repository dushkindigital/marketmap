package com.libereco.core.exceptions;

public class UserMarketplaceAuthorizationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private static final String MESSAGE = "No authorization found for marketplace %s. Please first authenticate yourself with %s";

    public UserMarketplaceAuthorizationException(String marketplace) {
        super(String.format(MESSAGE, marketplace, marketplace));
    }

    public UserMarketplaceAuthorizationException(String marketplace, Throwable e) {
        super(String.format(MESSAGE, marketplace), e);
    }

}
