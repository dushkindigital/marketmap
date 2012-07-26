package com.libereco.springsocial.etsy.connect;

import org.springframework.social.connect.support.OAuth1ConnectionFactory;

import com.libereco.springsocial.etsy.api.EtsyApi;

public class EtsyConnectionFactory extends OAuth1ConnectionFactory<EtsyApi> {

    public EtsyConnectionFactory(String consumerKey, String consumerSecret) {
        super("etsy", new EtsyServiceProvider(consumerKey, consumerSecret), new EtsyAdapter());
    }

}
