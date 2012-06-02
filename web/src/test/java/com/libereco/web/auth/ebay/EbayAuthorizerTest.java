package com.libereco.web.auth.ebay;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.libereco.web.auth.MarketplaceAuthorizationException;
import com.libereco.web.auth.SignInDetails;

@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext-web.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("test")
public class EbayAuthorizerTest {

    @Autowired
    private EbayAuthorizer ebayAuthorizer;

    @Ignore
    @Test
    public void shouldFetchTokenWithValidSession() throws Exception {
        String sessionId = "5dEBAA**8e62d07a1370a471d212d532fffffd84";
        EbayToken ebayToken = ebayAuthorizer.fetchToken(sessionId);
        System.out.println(ebayToken);
        assertNotNull(ebayToken);
    }

    @Test
    public void shouldValidateThatEbayAuthorizerObjectIsCorrectlyInstantiated() throws Exception {
        assertNotNull(ebayAuthorizer);
        assertEquals("Freelancer-Freelanc-61fa-4-utwirrh", ebayAuthorizer.getRuName());
        assertEquals("https://signin.sandbox.ebay.com/ws/eBayISAPI.dll?SignIn&RuName=", ebayAuthorizer.getSignInUrl());
    }

    @Test
    public void shouldGetNotNullSessionIdWithValidCredentials() throws Exception {
        String sessionId = ebayAuthorizer.getSessionId();
        assertNotNull(sessionId);
    }

    @Test
    public void shouldGetNotNullSigninDetailsWithValidCredentials() throws MarketplaceAuthorizationException {
        SignInDetails signInDetails = ebayAuthorizer.getSignInDetails();
        System.out.println(signInDetails);
        assertNotNull(signInDetails);
        assertNull(signInDetails.getSecretToken());
        assertNotNull(signInDetails.getSignInUrl());
        assertNotNull(signInDetails.getToken());
    }

    @Test
    @Ignore
    public void testFetchToken() throws Exception {
        SignInDetails signInDetails = ebayAuthorizer.getSignInDetails();
        System.out.println("Please open the url below in a browser and authorize the app...");
        System.out.println(signInDetails.getSignInUrl());
        waitForSpace();
        EbayToken ebayToken = ebayAuthorizer.fetchToken(signInDetails.getToken());
        assertNotNull(ebayToken);
        System.out.println(ebayToken);
        assertNotNull(ebayToken.getToken());
        assertNotNull(ebayToken.getExpirationTime());
    }

    public void waitForSpace() throws Exception {
        System.out.println("Press Enter to Fetch Token.....");
        BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
        consoleReader.readLine();
    }
}
