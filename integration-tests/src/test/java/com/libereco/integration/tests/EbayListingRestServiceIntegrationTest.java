package com.libereco.integration.tests;

import static com.jayway.restassured.RestAssured.given;
import static com.libereco.integration.tests.JsonUtils.toJson;
import static com.libereco.integration.tests.TestDataUtils.shouldAutheticateWithEbay;
import static com.libereco.integration.tests.TestDataUtils.shouldCreateLiberecoListing;
import static com.libereco.integration.tests.TestDataUtils.shouldCreateMarketplace;
import static com.libereco.integration.tests.TestDataUtils.shouldCreateUser;
import static org.hamcrest.Matchers.equalTo;

import java.util.Arrays;
import java.util.UUID;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mortbay.jetty.Server;

import com.google.common.collect.ImmutableMap;
import com.jayway.restassured.authentication.FormAuthConfig;
import com.jayway.restassured.response.Header;

public class EbayListingRestServiceIntegrationTest {

    private static Server server;
    private String listingName;

    @BeforeClass
    public static void startJettyServer() throws Exception {
        server = ServerUtils.startServer(8080, "/libereco",
                "../web/target/libereco.war");
    }

    @AfterClass
    public static void shutdownJettyServer() throws Exception {
        server.stop();
    }

    @Before
    public void setup() throws Exception {
        shouldCreateUser();
        shouldCreateMarketplace();
        shouldAutheticateWithEbay();
        listingName = "Test Listing " + UUID.randomUUID().toString();
        shouldCreateLiberecoListing(listingName);
    }

    @Test
    public void shouldCreateReadUpdateAndDeleteEbayListing() throws Exception {

        // CREATE EBAY_LISTING

        FormAuthConfig config = new FormAuthConfig("/libereco/resources/j_spring_security_check", "j_username", "j_password");

        String liberecoListingJson = toJson(ImmutableMap.<String, String> builder().
                put("name", listingName).put("id", "1").put("version", "0").
                build());

        String ebayListingJson = toJson(ImmutableMap.<String, String> builder().put("returnPolicy", "NO_RETURN").put("dispatchTimeMax", "3")
                .put("startPrice", "100").put("paypalEmail", "test@gmail.com")
                .put("lotSize", "1").put("listingDuration", "DAYS_3").put("liberecoListing", liberecoListingJson).build());

        given().
                auth().form("test_user", "password", config).
                contentType("application/json").header(new Header("Accept", "application/json")).
                body(ebayListingJson).
                expect().
                statusCode(201).
                log().all().
                post("/libereco/ebaylistings");

        // READ EBAY_LISTING

        given().log().all().
                auth().form("test_user", "password", config).
                contentType("application/json").header(new Header("Accept", "application/json")).
                expect().
                statusCode(200).
                body("id", equalTo(1)).
                body("returnPolicy", equalTo("NO_RETURN")).
                body("paypalEmail", equalTo("test@gmail.com")).
                body("listingDuration", equalTo("DAYS_3")).
                log().all().
                when().get("/libereco/ebaylistings/1");

        // READ EBAY_LISTING WHICH DOES NOT EXIST RETURNS ERROR CODE 404

        given().log().all().
                auth().form("test_user", "password", config).
                contentType("application/json").header(new Header("Accept", "application/json")).
                expect().
                statusCode(404).
                log().all().
                when().get("/libereco/ebaylistings/12345");

        // LIST ALL EBAY_LISTINGS

        given().log().all().
                auth().form("test_user", "password", config).
                contentType("application/json").header(new Header("Accept", "application/json")).
                expect().
                statusCode(200).
                body("id", equalTo(Arrays.asList(1))).
                body("returnPolicy", equalTo(Arrays.asList("NO_RETURN"))).
                body("paypalEmail", equalTo(Arrays.asList("test@gmail.com"))).
                body("listingDuration", equalTo((Arrays.asList("DAYS_3")))).
                log().all().
                when().get("/libereco/ebaylistings");

        // UPDATE EBAY_LISTING

        String ebayListingUpdateJson = toJson(ImmutableMap.<String, String> builder().put("id", "1").put("returnPolicy", "THIRTY_DAY_RETURN")
                .put("dispatchTimeMax", "5")
                .put("startPrice", "1000").put("paypalEmail", "test_updated@gmail.com")
                .put("lotSize", "1").put("listingDuration", "DAYS_7").put("liberecoListing", liberecoListingJson).put("version", "0").build());

        given().log().all().
                auth().form("test_user", "password", config).
                contentType("application/json").header(new Header("Accept", "application/json")).
                body(ebayListingUpdateJson).
                expect().
                statusCode(200).
                log().all().
                put("/libereco/ebaylistings");

        // READ UPDATED EBAY_LISTING

        given().log().all().
                auth().form("test_user", "password", config).
                contentType("application/json").header(new Header("Accept", "application/json")).
                expect().
                statusCode(200).
                body("id", equalTo(1)).
                body("returnPolicy", equalTo("THIRTY_DAY_RETURN")).
                body("paypalEmail", equalTo("test_updated@gmail.com")).
                body("listingDuration", equalTo("DAYS_7")).
                log().all().
                when().get("/libereco/ebaylistings/1");

        // DELETE EBAY_LISTING

        given().log().all().
                auth().form("test_user", "password", config).
                contentType("application/json").header(new Header("Accept", "application/json")).
                expect().
                statusCode(200).
                log().all().
                when().delete("/libereco/ebaylistings/1");

        given().log().all().
                auth().form("test_user", "password", config).
                contentType("application/json").header(new Header("Accept", "application/json")).
                expect().
                statusCode(404).
                log().all().
                when().delete("/libereco/ebaylistings/1");

    }

}
