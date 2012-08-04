package com.libereco.springsocial.ebay.connect;

import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.social.oauth1.AuthorizedRequestToken;
import org.springframework.social.oauth1.OAuth1Operations;
import org.springframework.social.oauth1.OAuth1Parameters;
import org.springframework.social.oauth1.OAuth1Version;
import org.springframework.social.oauth1.OAuthToken;
import org.springframework.util.MultiValueMap;

import com.ebay.sdk.ApiContext;
import com.ebay.sdk.call.FetchTokenCall;
import com.ebay.sdk.call.GetSessionIDCall;

public class EbayOAuth1Template implements OAuth1Operations {

    private Logger logger = LoggerFactory.getLogger(EbayOAuth1Template.class);

    private ApiContext apiContext;

    private String ruName;

    private String signInUrl;

    private OAuth1Version oauth1Version;

    public EbayOAuth1Template(String ruName, String signInUrl, ApiContext apiContext) {
        this.apiContext = apiContext;
        this.ruName = ruName;
        this.signInUrl = signInUrl;
        this.oauth1Version = OAuth1Version.CORE_10_REVISION_A;
    }

    @Override
    public OAuth1Version getVersion() {
        return oauth1Version;
    }

    @Override
    public OAuthToken fetchRequestToken(String callbackUrl, MultiValueMap<String, String> additionalParameters) {
        GetSessionIDCall gsidCall = new GetSessionIDCall(apiContext);
        gsidCall.setRuName(this.ruName);
        try {
            String sessionId = gsidCall.getSessionID();
            logger.debug("Received sessionId: " + sessionId);
            return new OAuthToken(sessionId, null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String buildAuthorizeUrl(String requestToken, OAuth1Parameters parameters) {
        return signInUrl + ruName + "&SessID=" + requestToken;
    }

    @Override
    public String buildAuthenticateUrl(String requestToken, OAuth1Parameters parameters) {
        return null;
    }

    @Override
    public OAuthToken exchangeForAccessToken(AuthorizedRequestToken requestToken, MultiValueMap<String, String> additionalParameters) {
        OAuthToken token = null;
        FetchTokenCall ftCall = new FetchTokenCall(apiContext);
        ftCall.setSessionID(requestToken.getValue());
        try {
            ftCall.fetchToken();
        } catch (Exception e) {
            throw new RuntimeException("Not able to fetch token from ebay.", e);
        }

        String returnedToken = ftCall.getReturnedToken();
        Calendar hardExpirationTime = ftCall.getHardExpirationTime();
        token = new OAuthToken(returnedToken, returnedToken);

        logger.debug("Ebay token: " + token);

        return token;
    }

}
