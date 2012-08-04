package com.libereco.springsocial.ebay.connect;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import com.ebay.sdk.ApiAccount;
import com.ebay.sdk.ApiContext;
import com.ebay.sdk.ApiCredential;
import com.ebay.sdk.ApiLogging;

/**
 * This is a Spring configuration class which is responsible for creating beans
 * for different MarketPlace apis.
 * 
 * @author shekhar
 * 
 */
@Configuration
@PropertySource("classpath:libereco.properties")
public class MarketplacesApiConfiguration {

    @Autowired
    Environment environment;

    @Bean
    public ApiContext apiContext() {
        ApiContext apiContext = new ApiContext();
        apiContext.setApiCredential(apiCredential());
        apiContext.setApiLogging(apiLogging());
        apiContext.setApiServerUrl(environment.getProperty("libereco.ebay.apiServerUrl"));
        return apiContext;
    }

    @Bean
    public ApiLogging apiLogging() {
        ApiLogging apiLogging = new ApiLogging();
        apiLogging.setLogExceptions(environment.getProperty("libereco.ebay.log.exceptions", Boolean.class));
        apiLogging.setLogHTTPHeaders(environment.getProperty("libereco.ebay.log.httpheaders", Boolean.class));
        apiLogging.setLogSOAPMessages(environment.getProperty("libereco.ebay.log.soapmessages", Boolean.class));
        return apiLogging;
    }

    @Bean
    public ApiCredential apiCredential() {
        ApiCredential apiCredential = new ApiCredential();
        apiCredential.setApiAccount(apiAccount());
        return apiCredential;
    }

    @Bean
    public ApiAccount apiAccount() {
        ApiAccount apiAccount = new ApiAccount();
        apiAccount.setApplication(environment.getProperty("libereco.ebay.application"));
        apiAccount.setCertificate(environment.getProperty("libereco.ebay.certificate"));
        apiAccount.setDeveloper(environment.getProperty("libereco.ebay.developer"));
        return apiAccount;
    }
}
