package com.libereco.springsocial.ebay.connect;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.social.connect.Connection;
import org.springframework.social.oauth1.AuthorizedRequestToken;
import org.springframework.social.oauth1.OAuth1Operations;
import org.springframework.social.oauth1.OAuth1Parameters;
import org.springframework.social.oauth1.OAuthToken;

import com.ebay.sdk.ApiAccount;
import com.ebay.sdk.ApiContext;
import com.ebay.sdk.ApiCredential;
import com.ebay.sdk.ApiLogging;
import com.libereco.springsocial.ebay.api.EbayApi;

public class EbayConnectionFactoryTest {

    @Test
    public void shouldEstablishConnectionWithEbay() throws Exception {
        String runName = "Freelancer-Freelanc-61fa-4-utwirrh";
        String signInUrl = "https://signin.sandbox.ebay.com/ws/eBayISAPI.dll?SignIn&RuName=";
        ApiContext apiContext = new ApiContext();
        apiContext.setApiCredential(apiCredential());
        apiContext.setApiLogging(apiLogging());
        apiContext.setApiServerUrl("https://api.sandbox.ebay.com/wsapi");
        EbayConnectionFactory connectionFactory = new EbayConnectionFactory(runName, signInUrl, apiContext);
        OAuth1Operations oAuth1Operations = connectionFactory.getOAuthOperations();
        OAuthToken requestToken = oAuth1Operations.fetchRequestToken("oob", null);
        assertNotNull(requestToken);
        
        String authorizeUrl = oAuth1Operations.buildAuthorizeUrl(requestToken.getValue(), null);
        System.out.println(authorizeUrl);

        System.out.println("Enter the PIN code and hit ENTER when you're done:");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String pin = br.readLine();
        
        AuthorizedRequestToken authorizedRequestToken = new AuthorizedRequestToken(requestToken, pin);

        Map<String, List<String>> parameters = new HashMap<String, List<String>>();
        OAuthToken accessToken = oAuth1Operations.exchangeForAccessToken(authorizedRequestToken, new OAuth1Parameters(parameters));

        Connection<EbayApi> ebayConnection = connectionFactory.createConnection(accessToken);
        EbayApi api = ebayConnection.getApi();

        assertNotNull(ebayConnection);
        assertTrue(ebayConnection.test());

    }

    private ApiLogging apiLogging() {
        ApiLogging apiLogging = new ApiLogging();
        apiLogging.setLogExceptions(true);
        apiLogging.setLogHTTPHeaders(true);
        apiLogging.setLogSOAPMessages(true);
        return apiLogging;
    }

    private ApiCredential apiCredential() {
        ApiCredential apiCredential = new ApiCredential();
        apiCredential.setApiAccount(apiAccount());
        return apiCredential;
    }

    private ApiAccount apiAccount() {
        ApiAccount apiAccount = new ApiAccount();
        apiAccount.setApplication("Freelanc-61fa-4cbb-88b3-10baba6c87ee");
        apiAccount.setCertificate("b6dd31c5-a7a4-45e9-879f-eced2b1daf48");
        apiAccount.setDeveloper("19f04b54-c411-41ca-8153-2b8de1a25594");
        return apiAccount;
    }
}
