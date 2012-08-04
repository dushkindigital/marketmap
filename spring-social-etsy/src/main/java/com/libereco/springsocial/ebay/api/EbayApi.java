package com.libereco.springsocial.ebay.api;

import org.springframework.social.ApiBinding;

public interface EbayApi extends ApiBinding {

    EbayUserOperations userOperations();

}
