package com.libereco.springsocial.ebay.api.impl;

import com.ebay.sdk.ApiContext;
import com.libereco.springsocial.ebay.api.EbayApi;
import com.libereco.springsocial.ebay.api.EbayUserOperations;

public class EbayTemplate implements EbayApi {

    private EbayUserOperations ebayUserOperations;
    private ApiContext apiContext;

    public EbayTemplate() {
        super();
        initSubApis();
    }

    public EbayTemplate(ApiContext apiContext) {
        this.apiContext = apiContext;
        initSubApis();
    }

    private void initSubApis() {
        ebayUserOperations = new EbayUserTemplate(apiContext);
    }

    @Override
    public EbayUserOperations userOperations() {
        return ebayUserOperations;
    }

    @Override
    public boolean isAuthorized() {
        return true;
    }

}
