package com.libereco.web.config;

import static org.junit.Assert.assertNotNull;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.web.ConnectController;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.libereco.springsocial.etsy.api.EtsyApi;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SocialConfig.class })
@ActiveProfiles("test")
public class SocialConfigTest {

    @Inject
    ConnectionFactoryLocator connectionFactoryLocator;

    @Inject
    UsersConnectionRepository usersConnectionRepository;

    @Inject
    ConnectionRepository connectionRepository;

    @Inject
    EtsyApi etsyApi;

    @Inject
    ConnectController connectController;

    @Test
    public void shouldAssertThatAllSocialConfigBeanAreInstantiated() {
        assertNotNull(connectionFactoryLocator);
        assertNotNull(usersConnectionRepository);
        assertNotNull(connectionRepository);
        assertNotNull(etsyApi);
        assertNotNull(connectController);
    }

}
