package com.libereco.web.auth;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ebay.sdk.ApiAccount;
import com.ebay.sdk.ApiContext;
import com.ebay.sdk.ApiCredential;
import com.ebay.sdk.ApiLogging;

@ContextConfiguration(classes = MarketplacesApiConfiguration.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles(profiles="test")
public class MarketplacesApiConfigurationTest {

    @Autowired
    private ApiContext apiContext;

    @Autowired
    private ApiLogging apiLogging;

    @Autowired
    private ApiCredential apiCredential;

    @Autowired
    private ApiAccount apiAccount;

    @Test
    public void testApiContext() {
        assertNotNull(apiContext);
        assertEquals("https://api.sandbox.ebay.com/wsapi", apiContext.getApiServerUrl());
    }

    @Test
    public void testApiLogging() {
        assertNotNull(apiLogging);
        assertTrue(apiLogging.isLogExceptions());
        assertTrue(apiLogging.isLogSOAPMessages());
        assertTrue(apiLogging.isLogHTTPHeaders());
    }

    @Test
    public void testApiCredential() {
        assertNotNull(apiCredential);
    }

    @Test
    public void testApiAccount() {
        assertNotNull(apiAccount);
        assertEquals("Freelanc-61fa-4cbb-88b3-10baba6c87ee", apiAccount.getApplication());
        assertEquals("b6dd31c5-a7a4-45e9-879f-eced2b1daf48", apiAccount.getCertificate());
        assertEquals("19f04b54-c411-41ca-8153-2b8de1a25594", apiAccount.getDeveloper());
    }

}
