package com.libereco.springsocial.ebay.connect;

import org.springframework.social.connect.support.OAuth1ConnectionFactory;

import com.ebay.sdk.ApiContext;
import com.libereco.springsocial.ebay.api.EbayApi;

public class EbayConnectionFactory extends OAuth1ConnectionFactory<EbayApi> {

    public EbayConnectionFactory(String runName, String signInUrl, ApiContext apiContext) {
        super("ebay", new EbayServiceProvider(runName, signInUrl, apiContext), new EbayAdapter());
    }

}
