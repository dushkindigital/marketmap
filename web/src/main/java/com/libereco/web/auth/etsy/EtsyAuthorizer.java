package com.libereco.web.auth.etsy;

import javax.inject.Inject;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;

import org.apache.log4j.Logger;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.libereco.core.exceptions.ExternalServiceException;
import com.libereco.web.auth.MarketplaceAuthorizer;
import com.libereco.web.auth.SignInDetails;

@Component
public class EtsyAuthorizer implements MarketplaceAuthorizer {

    private Environment environment;

    private final Logger logger = Logger.getLogger(this.getClass());

    private OAuthConsumer consumer;

    private OAuthProvider provider;

    @Inject
    public EtsyAuthorizer(Environment environment) {
        this.environment = environment;
        String consumer_key = environment.getProperty("libereco.etsy.consumer.key");
        String consumer_secret = environment.getProperty("libereco.etsy.consumer.secret");

        logger.debug("Consumer Key : " + consumer_key);
        logger.debug("Consumer Secret : " + consumer_secret);
        consumer = new CommonsHttpOAuthConsumer(consumer_key, consumer_secret);

        String requestTokenEndpoint = environment.getProperty("libereco.etsy.requestTokenEndpointUrl");
        String accessTokenEndpoint = environment.getProperty("libereco.etsy.accessTokenEndpointUrl");
        String authorizationUrl = environment.getProperty("libereco.etsy.authorizationWebsiteUrl");

        logger.debug("RequestTokenEndpoint : " + requestTokenEndpoint);
        logger.debug("AccessTokenEndpoint : " + accessTokenEndpoint);
        logger.debug("AuthorizationUrl : " + authorizationUrl);

        provider = new CommonsHttpOAuthProvider(requestTokenEndpoint,
                accessTokenEndpoint, authorizationUrl);
    }

    public SignInDetails getSignInDetails() throws ExternalServiceException {
        String authUrl = null;
        try {
            authUrl = provider.retrieveRequestToken(consumer, OAuth.OUT_OF_BAND);
        } catch (Exception e) {
            throw new ExternalServiceException("Not able to get authentication url from etsy.", e);
        }
        logger.debug("Authentication url returned by Etsy :" + authUrl);
        return new SignInDetails(consumer.getToken(), consumer.getConsumerSecret(), authUrl);
    }

    public EtsyToken getToken(String pin, SignInDetails signInDetails) {
        try {
            provider.retrieveAccessToken(consumer, pin);
        } catch (Exception e) {
            throw new ExternalServiceException("Not able to get token from etsy.", e);
        }
        logger.debug("Access token: " + consumer.getToken());
        logger.debug("Token secret: " + consumer.getTokenSecret());
        return new EtsyToken(consumer.getToken(), consumer.getTokenSecret());
    }
}