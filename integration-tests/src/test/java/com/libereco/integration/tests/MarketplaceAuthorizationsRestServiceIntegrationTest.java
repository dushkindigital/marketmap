package com.libereco.integration.tests;

import static com.jayway.restassured.RestAssured.given;
import static com.libereco.integration.tests.JsonUtils.toJson;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertNotNull;

import java.net.URL;
import java.util.Date;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mortbay.jetty.Server;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.google.common.collect.ImmutableMap;
import com.jayway.restassured.authentication.FormAuthConfig;
import com.jayway.restassured.response.Header;

public class MarketplaceAuthorizationsRestServiceIntegrationTest {

    private static Server server;

    @BeforeClass
    public static void startJettyServer() throws Exception {
        server = ServerUtils.startServer(8080, "/libereco", "../web/target/libereco.war");
    }

    @AfterClass
    public static void shutdownJettyServer() throws Exception {
        server.stop();
    }

    @Before
    public void setup() throws Exception {
        String userJson = toJson(ImmutableMap.<String, String> builder().put("username", "test_user").put("password", "password")
                .put("status", "ACTIVE").put("lastUpdated", new Date().toString()).build());

        // CREATE USER

        given().log().all().
                contentType("application/json").header(new Header("Accept", "application/json")).
                body(userJson).
                expect().
                statusCode(201).
                log().all().
                post("/libereco/liberecousers");

        String marketplaceJson = toJson(ImmutableMap.<String, String> builder().put("marketplaceName", "ebay")
                .put("marketplaceShortName", "ebay").build());

        given().log().all().
                contentType("application/json").header(new Header("Accept", "application/json")).
                body(marketplaceJson).
                expect().
                statusCode(201).
                log().all().
                post("/libereco/marketplaces");
    }

    @Test
    public void shouldCreateReadUpdateAndDeleteMarketplaceAuthorizations() throws Exception {

        // GET MARKETPLACE_AUTHORIZATIONS

        FormAuthConfig config = new FormAuthConfig("/libereco/resources/j_spring_security_check", "j_username", "j_password");
        String ebaySigninUrl = given().
                auth().form("test_user", "password", config).
                log().all().
                contentType("application/json").
                header(new Header("Accept", "application/json")).
                expect().
                statusCode(200).
                and().
                body(containsString("https://signin.sandbox.ebay.com/ws/eBayISAPI.dll?SignIn&RuName=Freelancer-Freelanc-61fa-4-utwirrh&SessID=")).
                log().all().
                get("/libereco/marketplaces/ebay/authorize").asString();

        // PROGRAMMATICALLY SIGNIN TO EBAY

        signinToEbay(ebaySigninUrl);

        // FETCH EBAY MARKETPLACE AUTHORIZATION TOKEN
        String token = given().
                auth().form("test_user", "password", config).
                log().all().
                contentType("application/json").
                header(new Header("Accept", "application/json")).
                expect().
                statusCode(201).
                log().all().
                get("/libereco/marketplaces/ebay/fetchToken").asString();

        assertNotNull(token);

        given().
                auth().form("test_user", "password", config).
                log().all().
                contentType("application/json").
                header(new Header("Accept", "application/json")).
                expect().
                body("token", is(notNullValue())).
                body("tokenSecret", is(equalTo(null))).
                body("expirationTime", is(notNullValue())).
                statusCode(200).
                log().all().
                get("/libereco/test_user/marketplaceauthorizations/ebay");

    }

    private void signinToEbay(String ebaySigninUrl) throws Exception {
        URL url = new URL(ebaySigninUrl);
        WebClient webClient = new WebClient();
        HtmlPage page = (HtmlPage) webClient.getPage(url);
        HtmlForm form = page.getFormByName("SignInForm");
        form.getInputByName("userid").setValueAttribute("testuser_shekhargulati");
        form.getInputByName("pass").setValueAttribute("Shekh@r7");

        page = (HtmlPage) form.getInputByValue("Sign in").click();

        form = page.getFormByName("frmAuth");
        System.out.println(form.asText());
        form.getInputByValue("I agree").click();

    }
}
