package com.libereco.integration.tests;

import static com.jayway.restassured.RestAssured.given;
import static com.libereco.integration.tests.JsonUtils.toJson;
import static com.libereco.integration.tests.JsonUtils.toJsonObject;
import static org.hamcrest.Matchers.equalTo;

import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mortbay.jetty.Server;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.jayway.restassured.response.Header;

public class MarketplaceRestServiceIntegrationTest {

    private Server server;

    @Before
    public void startJettyServer() throws Exception {
        server = ServerUtils.startServer(8080, "/libereco", "../web/target/libereco.war");
    }

    @After
    public void shutdownJettyServer() throws Exception {
        server.stop();
    }

    @Test
    public void shouldCreateReadUpdateAndDeleteMarketplace() throws Exception {

        // CREATE MARKETPLACE

        String marketplaceJson = toJson(ImmutableMap.<String, String> builder().put("marketplaceName", "test_marketplace")
                .put("marketplaceShortName", "test_marketplace_shortname").build());

        given().log().all().
                contentType("application/json").header(new Header("Accept", "application/json")).
                body(marketplaceJson).
                expect().
                statusCode(201).
                log().all().
                post("/libereco/marketplaces");

        // READ MARKETPLACE

        given().log().all().
                contentType("application/json").header(new Header("Accept", "application/json")).
                expect().
                statusCode(200).
                body("id", equalTo(1)).
                body("marketplaceName", equalTo("test_marketplace")).
                body("marketplaceShortName", equalTo("test_marketplace_shortname")).
                log().all().
                when().get("/libereco/marketplaces/1");

        // READ MARKETPLACE WHICH DOES NOT EXIST RETURNS ERROR CODE 404

        given().log().all().
                contentType("application/json").header(new Header("Accept", "application/json")).
                expect().
                statusCode(404).
                log().all().
                when().get("/libereco/marketplaces/12345");

        // CREATE MULTIPLE MARKETPLACES IN ONE REQUEST

        JsonArray array = new JsonArray();

        array.add(toJsonObject(ImmutableMap.<String, String> builder().put("marketplaceName", "test_marketplace_1")
                .put("marketplaceShortName", "test_marketplace_shortname_1").build()));
        array.add(toJsonObject(ImmutableMap.<String, String> builder().put("marketplaceName", "test_marketplace_2")
                .put("marketplaceShortName", "test_marketplace_shortname_2").build()));

        String jsonArray = array.toString();

        given().log().all().
                contentType("application/json").header(new Header("Accept", "application/json")).
                body(jsonArray).
                expect().
                statusCode(201).
                log().all().
                post("/libereco/marketplaces/jsonArray");

        // LIST ALL MARKETPLACES

        given().log().all().
                contentType("application/json").header(new Header("Accept", "application/json")).
                expect().
                statusCode(200).
                body("id", equalTo(Arrays.asList(1, 2, 3))).
                body("marketplaceName", equalTo(Arrays.asList("test_marketplace", "test_marketplace_1", "test_marketplace_2"))).
                body("marketplaceShortName",
                        equalTo(Arrays.asList("test_marketplace_shortname", "test_marketplace_shortname_1", "test_marketplace_shortname_2"))).
                log().all().
                when().get("/libereco/marketplaces");

        // UPDATE MARKETPLACE

        String marketplaceUpdateJson = toJson(ImmutableMap.<String, String> builder().put("id", "1").put("marketplaceName", "test_marketplace")
                .put("marketplaceShortName", "test_marketplace_shortname").put("version", "0").build());

        given().log().all().
                contentType("application/json").header(new Header("Accept", "application/json")).
                body(marketplaceUpdateJson).
                expect().
                statusCode(200).
                log().all().
                put("/libereco/marketplaces");

        // DELETE MARKETPLACE

        given().log().all().
                contentType("application/json").header(new Header("Accept", "application/json")).
                expect().
                statusCode(200).
                log().all().
                when().delete("/libereco/marketplaces/1");

        given().log().all().
                contentType("application/json").header(new Header("Accept", "application/json")).
                expect().
                statusCode(404).
                log().all().
                when().delete("/libereco/marketplaces/1");
    }

    @Test
    public void shouldThrowExceptionWhenMarketplaceIsCreatedWithSameName() throws Exception {
        String marketplaceJson = toJson(ImmutableMap.<String, String> builder().put("marketplaceName", "test_marketplace_duplicate")
                .put("marketplaceShortName", "test_marketplace_shortname").build());

        given().log().all().
                contentType("application/json").header(new Header("Accept", "application/json")).
                body(marketplaceJson).
                expect().
                statusCode(201).
                log().all().
                post("/libereco/marketplaces");

        given().log().all().
                contentType("application/json").header(new Header("Accept", "application/json")).
                body(marketplaceJson).
                expect().
                statusCode(405).
                log().all().
                post("/libereco/marketplaces");
        given().log().all().
                contentType("application/json").header(new Header("Accept", "application/json")).
                expect().
                statusCode(200).
                log().all().
                when().delete("/libereco/marketplaces/1");
    }

}
