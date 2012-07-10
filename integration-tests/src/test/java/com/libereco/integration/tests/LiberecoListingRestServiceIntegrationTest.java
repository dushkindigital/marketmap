package com.libereco.integration.tests;

import static com.jayway.restassured.RestAssured.given;
import static com.libereco.integration.tests.JsonUtils.toJson;
import static com.libereco.integration.tests.JsonUtils.toJsonObject;
import static com.libereco.integration.tests.TestDataUtils.shouldAutheticateWithEbay;
import static com.libereco.integration.tests.TestDataUtils.shouldCreateMarketplace;
import static com.libereco.integration.tests.TestDataUtils.shouldCreateUser;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.UUID;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mortbay.jetty.Server;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.jayway.restassured.authentication.FormAuthConfig;
import com.jayway.restassured.response.Header;

public class LiberecoListingRestServiceIntegrationTest {

    private static Server server;

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
        // shouldCreateShippingMethod();
        // shouldCreatePaymentInformation();
    }

    @Test
    public void shouldCreateLiberecoListing() throws Exception {

        FormAuthConfig config = new FormAuthConfig("/libereco/resources/j_spring_security_check", "j_username", "j_password");

        String itemLocationJson = toJson(ImmutableMap.<String, String> builder().put("itemLocation", "SanJose, CA").put("postalCode", "95125")
                .build());

        JsonArray shippingInformationJsonArray = new JsonArray();
        shippingInformationJsonArray.add(toJsonObject(ImmutableMap.<String, String> builder().put("shippingCost", "2.5")
                .put("shippingService", "USPSMedia").put("shippingType", "FLAT").build()));

        String shippingInformationJson = shippingInformationJsonArray.toString();

        JsonArray paymentInformationJsonArray = new JsonArray();
        paymentInformationJsonArray.add(toJsonObject(ImmutableMap.<String, String> builder().put("paymentMethod", "AM_EX").build()));
        String paymentInformationJson = paymentInformationJsonArray.toString();

        String listingName = "test_listing" + UUID.randomUUID().toString();

        String liberecoListingJson = toJson(ImmutableMap.<String, String> builder().
                put("name", listingName).
                put("price", "100").
                put("quantity", "1").
                put("description", "test listing").
                put("category", "CAT_ELECTRONICS").
                put("listingCondition", "NEW").
                put("itemLocation", itemLocationJson).
                put("shippingInformations", shippingInformationJson).
                put("liberecoPaymentInformations", paymentInformationJson).
                build());

        given().log().all().
                auth().form("test_user", "password", config).
                multiPart("picture", new File("src/test/resources/samsung-galaxy.jpg")).
                multiPart("json", liberecoListingJson).
                expect().
                statusCode(201).
                log().all().
                post("/libereco/liberecolistings");

        InputStream inputStream = given().log().all().
                auth().form("test_user", "password", config).
                expect().
                get("/libereco/liberecolistings/1/image/samsung-galaxy.jpg").asInputStream();

        assertNotNull(inputStream);

        // READ LIBERECO_LISTING

        given().log().all().
                auth().form("test_user", "password", config).
                contentType("application/json").header(new Header("Accept", "application/json")).
                expect().
                statusCode(200).
                body("name", equalTo(listingName)).
                body("quantity", equalTo(1)).
                body("description", equalTo("test listing")).
                body("category", equalTo("CAT_ELECTRONICS")).
                body("listingCondition", equalTo("NEW")).
                body("pictureName", equalTo("samsung-galaxy.jpg")).
                body("pictureUrl", equalTo("http://localhost:8080/libereco/liberecolistings/1/image/samsung-galaxy.jpg")).
                body("userId", equalTo(1)).
                log().all().
                when().get("/libereco/liberecolistings/1");

        // READ LIBERECO_LISTING WHICH DOES NOT EXIST RETURNS ERROR CODE 404

        given().log().all().
                auth().form("test_user", "password", config).
                contentType("application/json").header(new Header("Accept", "application/json")).
                expect().
                statusCode(404).
                log().all().
                when().get("/libereco/liberecolistings/12345");

        // LIST ALL LIBERECO_LISTING

        given().log().all().
                auth().form("test_user", "password", config).
                contentType("application/json").header(new Header("Accept", "application/json")).
                expect().
                statusCode(200).
                body("name", equalTo(Arrays.asList(listingName))).
                body("quantity", equalTo(Arrays.asList(1))).
                body("description", equalTo(Arrays.asList("test listing"))).
                body("category", equalTo(Arrays.asList("CAT_ELECTRONICS"))).
                body("listingCondition", equalTo(Arrays.asList("NEW"))).
                body("pictureName", equalTo(Arrays.asList("samsung-galaxy.jpg"))).
                body("pictureUrl", equalTo(Arrays.asList("http://localhost:8080/libereco/liberecolistings/1/image/samsung-galaxy.jpg"))).
                body("userId", equalTo(Arrays.asList(1))).
                log().all().
                when().get("/libereco/liberecolistings");
    }
}
