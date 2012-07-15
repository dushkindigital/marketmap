package com.libereco.integration.tests;

import static com.jayway.restassured.RestAssured.given;
import static com.libereco.integration.tests.JsonUtils.toJson;
import static com.libereco.integration.tests.TestDataUtils.shouldCreateLiberecoListing;
import static com.libereco.integration.tests.TestDataUtils.shouldCreateMarketplace;
import static com.libereco.integration.tests.TestDataUtils.shouldCreateUser;
import static com.libereco.integration.tests.TestDataUtils.shouldDeleteMarketplace;
import static com.libereco.integration.tests.TestDataUtils.shouldDeleteUser;
import static com.libereco.integration.tests.TestDataUtils.shouldFetchToken;
import static com.libereco.integration.tests.TestDataUtils.shouldGetSessionId;
import static com.libereco.integration.tests.TestDataUtils.signinToEbay;

import java.util.UUID;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mortbay.jetty.Server;

import com.google.common.collect.ImmutableMap;
import com.jayway.restassured.authentication.FormAuthConfig;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Header;

public class LiberecoBugVerificationTests {

    private static Server server;
    private static String userId;
    private static String marketplaceId;

    @BeforeClass
    public static void startJettyServer() throws Exception {
        server = ServerUtils.startServer(8080, "/libereco", "../web/target/libereco.war");
        userId = shouldCreateUser();
        marketplaceId = shouldCreateMarketplace();

        String ebaySigninUrl = shouldGetSessionId();

        // PROGRAMMATICALLY SIGNIN TO EBAY

        signinToEbay(ebaySigninUrl);

        shouldFetchToken();
    }

    @AfterClass
    public static void shutdownServer() throws Exception {
        shouldDeleteMarketplace(marketplaceId);
        shouldDeleteUser(userId);
        server.stop();
    }

    @Test
    public void shouldNotGiveExceptionIfFetchTokenIsCalledMoreThanOnce_Issue_38() throws Exception {

        FormAuthConfig config = new FormAuthConfig("/libereco/resources/j_spring_security_check", "j_username", "j_password");
        // FETCH EBAY MARKETPLACE AUTHORIZATION TOKEN
        given().
                auth().form("test_user", "password", config).
                log().all().
                contentType("application/json").
                header(new Header("Accept", "application/json")).
                expect().
                statusCode(201).
                log().all().
                get("/libereco/marketplaces/ebay/fetchToken");

    }

    @Test
    public void shouldNotThrowNullPointerExceptionWhenLiberecoListingIsCreatedWithoutMarketplaceAuthorization() throws Exception {

        String listingName = "Test Listing " + UUID.randomUUID().toString();
        shouldCreateLiberecoListing(listingName, userId);

    }

    @Test
    public void shouldGetErrorResponseWhenEbayListingsCreatedWithoutMarketplaceAuthorization() throws Exception {

        String userId = shouldCreateUser("test_user_1", "password");

        String listingName = "Test Listing " + UUID.randomUUID().toString();
        String json = shouldCreateLiberecoListing(listingName, userId, "test_user_1", "password");
        JsonPath jsonPath = new JsonPath(json);
        String liberecoListingId = jsonPath.getString("id");
        String liberecoListingVersionId = jsonPath.getString("version");

        FormAuthConfig config = new FormAuthConfig("/libereco/resources/j_spring_security_check", "j_username", "j_password");
        String liberecoListingJson = toJson(ImmutableMap.<String, String> builder().
                put("name", listingName).put("id", liberecoListingId).put("version", liberecoListingVersionId).
                build());

        String ebayListingJson = toJson(ImmutableMap.<String, String> builder().put("returnPolicy", "NO_RETURN").put("dispatchTimeMax", "3")
                .put("startPrice", "100").put("paypalEmail", "test@gmail.com")

                .put("lotSize", "1").put("listingDuration", "DAYS_3").put("liberecoListing", liberecoListingJson).build());
        given().
                auth().form("test_user_1", "password", config).
                contentType("application/json").header(new Header("Accept", "application/json")).
                body(ebayListingJson).
                expect().
                statusCode(401).
                log().all().
                post("/libereco/ebaylistings");

        shouldDeleteUser(userId);

    }
}
