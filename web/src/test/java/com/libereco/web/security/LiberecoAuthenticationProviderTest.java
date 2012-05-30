package com.libereco.web.security;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.libereco.core.domain.LiberecoUser;
import com.libereco.core.service.LiberecoUserService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext.xml",
        "classpath:/META-INF/spring/applicationContext-security.xml", "classpath:/META-INF/spring/applicationContext-jpa.xml" })
@Transactional
@ActiveProfiles("test")
public class LiberecoAuthenticationProviderTest {

    @Autowired
    private LiberecoAuthenticationProvider liberecoAuthenticationProvider;

    @Autowired
    private LiberecoUserService liberecoUserService;

    @Before
    public void setup() {
        liberecoUserService.saveLiberecoUser(new LiberecoUser("test_user", "password"));
    }

    @Test
    public void shouldRetrieveUserStringUsernamePasswordAuthenticationToken() {
        assertNotNull(liberecoAuthenticationProvider);
        Authentication authentication = new UsernamePasswordAuthenticationToken("test_user", "password");
        Authentication authenticatedUser = liberecoAuthenticationProvider.authenticate(authentication);
        assertNotNull(authenticatedUser);
    }

    @Test(expected = BadCredentialsException.class)
    public void shouldThrowExceptionWhenAuthenticationTokenIsInValid() {
        assertNotNull(liberecoAuthenticationProvider);
        Authentication authentication = new UsernamePasswordAuthenticationToken("test_user", "wrong_password");
        liberecoAuthenticationProvider.authenticate(authentication);
    }

}
