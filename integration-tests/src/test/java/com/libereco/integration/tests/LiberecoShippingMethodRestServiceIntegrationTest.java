package com.libereco.integration.tests;

import static com.jayway.restassured.RestAssured.given;
import static com.libereco.integration.tests.JsonUtils.toJson;
import static org.hamcrest.Matchers.equalTo;

import java.util.Arrays;
import java.util.Date;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mortbay.jetty.Server;

import com.google.common.collect.ImmutableMap;
import com.jayway.restassured.authentication.FormAuthConfig;
import com.jayway.restassured.response.Header;

public class LiberecoShippingMethodRestServiceIntegrationTest {

    private static Server server;

    @BeforeClass
    public static void startJettyServer() throws Exception {
        server = ServerUtils.startServer(8080, "/libereco", "../web/target/libereco.war");
    }

    @AfterClass
    public static void shutdownServer() throws Exception {
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

    }

    @Test
    public void shouldCreateReadUpdateAndDeleteLiberecoShippingInformation() throws Exception {

        // CREATE SHIPPING_INFORMATION
        FormAuthConfig config = new FormAuthConfig("/libereco/resources/j_spring_security_check", "j_username", "j_password");

        String shippingInformationJson = toJson(ImmutableMap.<String, String> builder().put("shippingType", "FLAT")
                .put("shippingService", "USPSMedia").put("shippingCost", "2.50").build());

        given().
                auth().form("test_user", "password", config).
                contentType("application/json").header(new Header("Accept", "application/json")).
                body(shippingInformationJson).
                expect().
                statusCode(201).
                log().all().
                post("/libereco/liberecolisting/shippinginformations");

        // READ SHIPPING_INFORMATION

        given().log().all().
                auth().form("test_user", "password", config).
                contentType("application/json").header(new Header("Accept", "application/json")).
                expect().
                statusCode(200).
                body("id", equalTo(1)).
                body("shippingType", equalTo("FLAT")).
                body("shippingService", equalTo("USPSMedia")).
                log().all().
                when().get("/libereco/liberecolisting/shippinginformations/1");

        // READ SHIPPING_INFORMATION WHICH DOES NOT EXIST RETURNS ERROR CODE 404

        given().log().all().
                auth().form("test_user", "password", config).
                contentType("application/json").header(new Header("Accept", "application/json")).
                expect().
                statusCode(404).
                log().all().
                when().get("/libereco/liberecolisting/shippinginformations/12345");

        // LIST ALL SHIPPING_INFORMATIONS

        given().log().all().
                auth().form("test_user", "password", config).
                contentType("application/json").header(new Header("Accept", "application/json")).
                expect().
                statusCode(200).
                body("id", equalTo(Arrays.asList(1))).
                body("shippingType", equalTo(Arrays.asList("FLAT"))).
                body("shippingService", equalTo(Arrays.asList("USPSMedia"))).
                log().all().
                when().get("/libereco/liberecolisting/shippinginformations");

        // UPDATE SHIPPING_INFORMATION

        String shippingInformationUpdateJson = toJson(ImmutableMap.<String, String> builder().put("id", "1").put("shippingType", "CALCULATED")
                .put("shippingService", "USPSMedia").put("shippingCost", "2.50").put("version", "0").build());

        given().log().all().
                auth().form("test_user", "password", config).
                contentType("application/json").header(new Header("Accept", "application/json")).
                body(shippingInformationUpdateJson).
                expect().
                statusCode(200).
                log().all().
                put("/libereco/liberecolisting/shippinginformations");

        // DELETE SHIPPING_INFORMATION

        given().log().all().
                auth().form("test_user", "password", config).
                contentType("application/json").header(new Header("Accept", "application/json")).
                expect().
                statusCode(200).
                log().all().
                when().delete("/libereco/liberecolisting/shippinginformations/1");

        given().log().all().
                auth().form("test_user", "password", config).
                contentType("application/json").header(new Header("Accept", "application/json")).
                expect().
                statusCode(404).
                log().all().
                when().delete("/libereco/liberecolisting/shippinginformations/1");
    }
}
