package com.libereco.web.auth.ebay;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.ebay.sdk.ApiContext;
import com.ebay.sdk.call.FetchTokenCall;
import com.ebay.sdk.call.GetSessionIDCall;
import com.libereco.core.exceptions.ExternalServiceException;
import com.libereco.web.auth.MarketplaceAuthorizer;
import com.libereco.web.auth.SignInDetails;

/**
 * This class does authorization with Ebay.
 * 
 * @author shekhar
 * 
 */
@Component
public class EbayAuthorizer implements MarketplaceAuthorizer {

    private Logger logger = LoggerFactory.getLogger(EbayAuthorizer.class);

    private ApiContext apiContext;

    private Environment environment;

    private String ruName;

    private String signInUrl;

    @Autowired
    public EbayAuthorizer(ApiContext apiContext, Environment environment) {
        this.apiContext = apiContext;
        this.environment = environment;
        this.ruName = environment.getProperty("libereco.ebay.ruName");
        this.signInUrl = environment.getProperty("libereco.ebay.signinUrl");
    }

    public String getSessionId() throws Exception {
        GetSessionIDCall gsidCall = new GetSessionIDCall(apiContext);
        gsidCall.setRuName(this.ruName);
        String sessionId = gsidCall.getSessionID();
        logger.debug("Received sessionId: " + sessionId);

        return sessionId;
    }

    public EbayToken fetchToken(String sessionId) {
        EbayToken token = null;
        FetchTokenCall ftCall = new FetchTokenCall(apiContext);
        ftCall.setSessionID(sessionId);
        try {
            ftCall.fetchToken();
        } catch (Exception e) {
            throw new ExternalServiceException("Not able to fetch token from ebay.", e);
        }

        token = new EbayToken(ftCall.getReturnedToken(),
                ftCall.getHardExpirationTime());

        logger.debug("Ebay token: " + token);

        return token;
    }

    public String buildSignInUrl(String sessionId) {
        return signInUrl + ruName + "&SessID=" + sessionId;
    }

    @Override
    public SignInDetails getSignInDetails() {
        SignInDetails signInDetails = null;

        try {
            String sessionId = getSessionId();
            String signInUrl = buildSignInUrl(sessionId);
            signInDetails = new SignInDetails();
            signInDetails.setSignInUrl(signInUrl);
            signInDetails.setToken(sessionId);
        } catch (Exception e) {
            throw new ExternalServiceException("Not able to get sessionId from ebay. Please try again in some time.", e);
        }
        return signInDetails;
    }

    public String getRuName() {
        return ruName;
    }

    public String getSignInUrl() {
        return signInUrl;
    }

}
