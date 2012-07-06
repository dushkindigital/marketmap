package com.libereco.integration.tests;

import static com.jayway.restassured.RestAssured.given;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

import com.google.gson.Gson;
import com.jayway.restassured.response.Header;

public class LiberecoUserRestIntegrationTest {

    @Test
    public void shouldCreateLiberecoUserProfile() {
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        parameters.put("username", "test_user_007");
        parameters.put("password", "password");
        parameters.put("status", "ACTIVE");
        Gson gson = new Gson();
        String userJson = gson.toJson(parameters);
        System.out.println(userJson);
        given().log().all().contentType("application/json").header(new Header("Accept", "application/json")).body(userJson).expect().log().all().post("/libereco/liberecousers?form");
    }
    
}
