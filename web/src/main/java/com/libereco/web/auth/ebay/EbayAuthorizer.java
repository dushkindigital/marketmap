package com.libereco.web.auth.ebay;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.ebay.sdk.ApiContext;
import com.ebay.sdk.call.FetchTokenCall;
import com.ebay.sdk.call.GetSessionIDCall;
import com.libereco.web.auth.MarketplaceAuthorizationException;
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

    // TODO: Define a more specific exception, eBay's SDK throws generic
    // exception
    public EbayToken fetchToken(String sessionId) throws Exception {
        EbayToken token = null;
        FetchTokenCall ftCall = new FetchTokenCall(apiContext);
        ftCall.setSessionID(sessionId);

        // Invoke the method that fetches the token and populate token
        // information in the call object
        ftCall.fetchToken();

        token = new EbayToken(ftCall.getReturnedToken(),
                ftCall.getHardExpirationTime());

        logger.debug("Ebay token: " + token);

        return token;
    }

    public String buildSignInUrl(String sessionId) {
        return signInUrl + ruName + "&SessID=" + sessionId;
    }

    @Override
    public SignInDetails getSignInDetails() throws MarketplaceAuthorizationException {
        SignInDetails signInDetails = null;

        try {
            String sessionId = getSessionId();
            String signInUrl = buildSignInUrl(sessionId);
            signInDetails = new SignInDetails();
            signInDetails.setSignInUrl(signInUrl);
            signInDetails.setToken(sessionId);
        } catch (Exception e) {
            logger.warn("Failed to get session id", e);
            throw new MarketplaceAuthorizationException(e);
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
