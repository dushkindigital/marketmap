package com.libereco.springsocial.etsy.connect;

import org.springframework.social.oauth1.OAuth1Template;
import org.springframework.social.oauth1.OAuth1Version;
import org.springframework.util.MultiValueMap;

public class EtsyOAuth1Template extends OAuth1Template {

    public EtsyOAuth1Template(String consumerKey, String consumerSecret, String requestTokenUrl, String authorizeUrl,
            String accessTokenUrl) {
        super(consumerKey, consumerSecret, requestTokenUrl, authorizeUrl, accessTokenUrl, OAuth1Version.CORE_10_REVISION_A);
    }

    @Override
    protected void addCustomAuthorizationParameters(MultiValueMap<String, String> parameters) {
//        parameters.add("oauth_consumer_key", getConsumerKey());
    }

}
