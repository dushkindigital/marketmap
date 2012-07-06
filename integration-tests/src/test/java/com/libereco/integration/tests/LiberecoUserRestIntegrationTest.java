package com.libereco.integration.tests;

import static com.jayway.restassured.RestAssured.given;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;

import com.google.gson.Gson;
import com.jayway.restassured.response.Header;

public class LiberecoUserRestIntegrationTest {

    private static final int PORT = 8080;
    private static Server server;
    
    @BeforeClass
    public static void startJettyServer() throws Exception {
        server = new Server(PORT);
        server.setStopAtShutdown(true);
        WebAppContext wac = new WebAppContext(getWarPath(), "/libereco");
        wac.setClassLoader(LiberecoUserRestIntegrationTest.class.getClass().getClassLoader());
        server.addHandler(wac);
        server.start();
    }
    
    private static String getWarPath() {
        String path = System.getProperty("libereco.war.path");
        path = StringUtils.isBlank(path) == true ? "../web/target/libereco.war" : path;
        return path;
    }
    
    @Test
    public void shouldCreateLiberecoUserProfile() {
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        parameters.put("username", "test_user_007");
        parameters.put("password", "password");
        parameters.put("status", "ACTIVE");
        Gson gson = new Gson();
        String userJson = gson.toJson(parameters);
        System.out.println(userJson);
        given().log().all().contentType("application/json").header(new Header("Accept", "application/json")).body(userJson).expect().log().all()
                .post("/libereco/liberecousers?form");
    }
    
    @AfterClass
    public static void shutdownServer() throws Exception{
        server.stop(); 
    }

}
