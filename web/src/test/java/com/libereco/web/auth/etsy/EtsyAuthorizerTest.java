package com.libereco.web.auth.etsy;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.libereco.web.auth.SignInDetails;

@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext-web.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
public class EtsyAuthorizerTest {

    @Autowired
    EtsyAuthorizer etsyAuthorizer;

    @Test
    public void testGetSignInDetails() throws Exception {
        SignInDetails signInDetails = etsyAuthorizer.getSignInDetails();
        assertThat(signInDetails, is(notNullValue()));
        assertThat(signInDetails.getSecretToken(), is(notNullValue()));
        assertThat(signInDetails.getToken(), is(notNullValue()));
        assertThat(signInDetails.getSignInUrl(), is(notNullValue()));

        System.out.println("Now visit:\n" + signInDetails.getSignInUrl()
                + "\n... and grant this app authorization");
        System.out.println("Enter the PIN code and hit ENTER when you're done:");

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String pin = br.readLine();

        System.out.println("Fetching access token from Etsy...");

        EtsyToken etsyToken = etsyAuthorizer.getToken(pin, signInDetails);
        assertThat(etsyToken.getAccessToken(), is(notNullValue()));
        assertThat(etsyToken.getTokenSecret(), is(notNullValue()));
    }

}
