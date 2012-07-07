package com.libereco.integration.tests;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mortbay.jetty.Server;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jayway.restassured.response.Header;

public class LiberecoUserRestServiceIntegrationTest {

    private static Server server;

    @BeforeClass
    public static void startJettyServer() throws Exception {
        server = ServerUtils.startServer(8080, "/libereco", "../web/target/libereco.war");
    }

    @Test
    public void shouldCreateReadUpdateAndDeleteLiberecoUser() throws Exception {
        String userJson = userJson("test_user_007", "password");

        // CREATE USER

        given().log().all().
                contentType("application/json").header(new Header("Accept", "application/json")).
                body(userJson).
                expect().
                statusCode(201).
                log().all().
                post("/libereco/liberecousers");

        // READ USER

        given().log().all().
                contentType("application/json").header(new Header("Accept", "application/json")).
                expect().
                statusCode(200).
                body("id", equalTo(1)).
                body("username", equalTo("test_user_007")).
                body("password", equalTo("password")).
                body("status", equalTo("ACTIVE")).
                log().all().
                when().get("/libereco/liberecousers/1");

        // READ USER WHICH DOES NOT EXIST RETURNS ERROR CODE 404

        given().log().all().
                contentType("application/json").header(new Header("Accept", "application/json")).
                expect().
                statusCode(404).
                log().all().
                when().get("/libereco/liberecousers/12345");

        // CREATE MULTIPLE USERS IN ONE REQUEST

        JsonArray elements = new JsonArray();
        JsonObject first = new JsonObject();
        first.addProperty("username", "test_user_008");
        first.addProperty("password", "password");
        first.addProperty("status", "ACTIVE");

        JsonObject second = new JsonObject();
        second.addProperty("username", "test_user_009");
        second.addProperty("password", "password");
        second.addProperty("status", "ACTIVE");

        elements.add(first);
        elements.add(second);

        String jsonArray = elements.toString();

        given().log().all().
                contentType("application/json").header(new Header("Accept", "application/json")).
                body(jsonArray).
                expect().
                statusCode(201).
                log().all().
                post("/libereco/liberecousers/jsonArray");

        // LIST ALL USERS

        given().log().all().
                contentType("application/json").header(new Header("Accept", "application/json")).
                expect().
                statusCode(200).
                body("id", equalTo(Arrays.asList(1, 2, 3))).
                body("username", equalTo(Arrays.asList("test_user_007", "test_user_008", "test_user_009"))).
                body("password", equalTo(Arrays.asList("password", "password", "password"))).
                body("status", equalTo(Arrays.asList("ACTIVE", "ACTIVE", "ACTIVE"))).
                log().all().
                when().get("/libereco/liberecousers");

        // UPDATE USER

        String userUpdateJson = userJsonWithId("test_user_7", "password", 1L);
        given().log().all().
                contentType("application/json").header(new Header("Accept", "application/json")).
                body(userUpdateJson).
                expect().
                statusCode(200).
                log().all().
                put("/libereco/liberecousers");

        // DELETE USER

        given().log().all().
                contentType("application/json").header(new Header("Accept", "application/json")).
                expect().
                statusCode(200).
                log().all().
                when().delete("/libereco/liberecousers/1");

        given().log().all().
                contentType("application/json").header(new Header("Accept", "application/json")).
                expect().
                statusCode(404).
                log().all().
                when().delete("/libereco/liberecousers/1");

    }

    @Test
    public void shouldThrowExceptionWhenUserIsCreatedWithSameUsername() throws Exception {
        String user = userJson("duplicate_user", "password");

        given().log().all().
                contentType("application/json").header(new Header("Accept", "application/json")).
                body(user).
                expect().
                statusCode(201).
                log().all().
                post("/libereco/liberecousers");

        given().log().all().
                contentType("application/json").header(new Header("Accept", "application/json")).
                body(user).
                expect().
                statusCode(405).
                log().all().
                post("/libereco/liberecousers");
        given().log().all().
                contentType("application/json").header(new Header("Accept", "application/json")).
                expect().
                statusCode(404).
                log().all().
                when().delete("/libereco/liberecousers/1");
    }

    private String userJson(String username, String password) {
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        parameters.put("username", username);
        parameters.put("password", password);
        parameters.put("status", "ACTIVE");
        parameters.put("lastUpdated", new Date().toString());
        Gson gson = new Gson();
        String userJson = gson.toJson(parameters);
        return userJson;
    }

    private String userJsonWithId(String username, String password, Long id) {
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        parameters.put("id", String.valueOf(id));
        parameters.put("username", username);
        parameters.put("password", password);
        parameters.put("status", "ACTIVE");
        parameters.put("lastUpdated", new Date().toString());
        parameters.put("version", String.valueOf(0));
        Gson gson = new Gson();
        String userJson = gson.toJson(parameters);
        return userJson;
    }

    @AfterClass
    public static void shutdownServer() throws Exception {
        server.stop();
    }

}
