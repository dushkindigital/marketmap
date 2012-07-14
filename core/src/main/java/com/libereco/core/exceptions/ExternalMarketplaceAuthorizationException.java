package com.libereco.core.exceptions;

public class ExternalMarketplaceAuthorizationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private static final String MESSAGE = "No authorization found for marketplace %s. Please first authenticate yourself with %s";

    public ExternalMarketplaceAuthorizationException(String marketplace) {
        super(String.format(MESSAGE, marketplace));
    }

    public ExternalMarketplaceAuthorizationException(String marketplace, Throwable e) {
        super(String.format(MESSAGE, marketplace), e);
    }

}
