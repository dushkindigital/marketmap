package com.libereco.springsocial.etsy.connect;

import org.springframework.social.oauth1.AbstractOAuth1ServiceProvider;

import com.libereco.springsocial.etsy.api.EtsyApi;
import com.libereco.springsocial.etsy.api.impl.EtsyTemplate;

public class EtsyServiceProvider extends AbstractOAuth1ServiceProvider<EtsyApi> {

    public EtsyServiceProvider(String consumerKey, String consumerSecret) {
        super(consumerKey, consumerSecret, new EtsyOAuth1Template(consumerKey, consumerSecret,
                "http://sandbox.openapi.etsy.com/v2/oauth/request_token",
                "http://www.etsy.com/oauth/signin", "http://sandbox.openapi.etsy.com/v2/oauth/access_token"));
    }

    @Override
    public EtsyTemplate getApi(String accessToken, String secret) {
        return new EtsyTemplate(getConsumerKey(), getConsumerSecret(), accessToken, secret);
    }

}
