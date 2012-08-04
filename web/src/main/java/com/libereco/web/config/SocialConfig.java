package com.libereco.web.config;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository;
import org.springframework.social.connect.support.ConnectionFactoryRegistry;

import com.ebay.sdk.ApiContext;
import com.libereco.springsocial.ebay.api.EbayApi;
import com.libereco.springsocial.ebay.api.impl.EbayTemplate;
import com.libereco.springsocial.ebay.connect.EbayConnectionFactory;
import com.libereco.springsocial.etsy.api.EtsyApi;
import com.libereco.springsocial.etsy.api.impl.EtsyTemplate;
import com.libereco.springsocial.etsy.connect.EtsyConnectionFactory;
import com.libereco.springsocial.etsy.connect.web.LiberecoConnectController;

/**
 * Etsy Spring Social Configuration.
 */
@Configuration
@PropertySource(value = "libereco.properties")
public class SocialConfig {

    @Inject
    private Environment environment;

    @Inject
    private DataSource dataSource;

    @Inject
    private ApiContext apiContext;

    @Bean
    @Scope(value = "singleton", proxyMode = ScopedProxyMode.INTERFACES)
    public ConnectionFactoryLocator connectionFactoryLocator() {
        ConnectionFactoryRegistry registry = new ConnectionFactoryRegistry();
        registry.addConnectionFactory(new EbayConnectionFactory(environment.getProperty("libereco.ebay.ruName"), environment
                .getProperty("libereco.ebay.signinUrl"), apiContext));
        registry.addConnectionFactory(new EtsyConnectionFactory(environment.getProperty("libereco.etsy.consumer.key"),
                environment.getProperty("libereco.etsy.consumer.secret")));
        return registry;
    }

    @Bean
    @Scope(value = "singleton", proxyMode = ScopedProxyMode.INTERFACES)
    public UsersConnectionRepository usersConnectionRepository() {
        return new JdbcUsersConnectionRepository(dataSource, connectionFactoryLocator(), Encryptors.noOpText());
    }

    @Bean
    @Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
    public ConnectionRepository connectionRepository() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new IllegalStateException("Unable to get a ConnectionRepository: no user signed in");
        }
        return usersConnectionRepository().createConnectionRepository(authentication.getName());
    }

    @Bean
    @Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
    public EbayApi ebayApi() {
        Connection<EbayApi> ebayApi = connectionRepository().findPrimaryConnection(EbayApi.class);
        return ebayApi != null ? ebayApi.getApi() : new EbayTemplate();
    }

    @Bean
    @Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
    public EtsyApi etsyApi() {
        Connection<EtsyApi> etsyApi = connectionRepository().findPrimaryConnection(EtsyApi.class);
        return etsyApi != null ? etsyApi.getApi() : new EtsyTemplate();
    }

    @Bean
    public LiberecoConnectController connectController() {
        LiberecoConnectController connectController = new LiberecoConnectController(connectionFactoryLocator(), connectionRepository());
        return connectController;
    }

}