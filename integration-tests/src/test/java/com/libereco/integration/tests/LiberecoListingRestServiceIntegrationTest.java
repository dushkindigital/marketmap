package com.libereco.integration.tests;

import static com.jayway.restassured.RestAssured.given;
import static com.libereco.integration.tests.JsonUtils.toJson;
import static com.libereco.integration.tests.TestDataUtils.shouldAutheticateWithEbay;
import static com.libereco.integration.tests.TestDataUtils.shouldCreateMarketplace;
import static com.libereco.integration.tests.TestDataUtils.shouldCreatePaymentInformation;
import static com.libereco.integration.tests.TestDataUtils.shouldCreateShippingMethod;
import static com.libereco.integration.tests.TestDataUtils.shouldCreateUser;

import java.io.File;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mortbay.jetty.Server;

import com.google.common.collect.ImmutableMap;
import com.jayway.restassured.authentication.FormAuthConfig;
import com.jayway.restassured.response.Header;

public class LiberecoListingRestServiceIntegrationTest {

//     private Server server;
//    
//     @Before
//     public void startJettyServer() throws Exception {
//     server = ServerUtils.startServer(8080, "/libereco",
//     "../web/target/libereco.war");
//     }
//    
//     @After
//     public void shutdownJettyServer() throws Exception {
//     server.stop();
//     }
//    
//     @Before
//     public void setup() throws Exception {
//     shouldCreateUser();
//     shouldCreateMarketplace();
//     shouldAutheticateWithEbay();
//     shouldCreateShippingMethod();
//     shouldCreatePaymentInformation();
//     }

    @Test
    public void test() throws Exception {
        
        FormAuthConfig config = new FormAuthConfig("/libereco/resources/j_spring_security_check", "j_username", "j_password");
        
        String liberecoListingJson = toJson(ImmutableMap.<String, String> builder().
                put("name", "test_listing" + UUID.randomUUID().toString()).
                put("price", "100").
                put("quantity", "1").
                put("description", "test listing").
                put("category", "CAT_ELECTRONICS").
                put("listingCondition", "New").
                put("itemLocation", "{'itemLocation':'SanJose, CA','postalCode':'95125'}").
                put("shippingInformations", "{'USPSMedia 2.5'}").
                put("liberecoPaymentInformations", "{'AM_EX'}").
                build());
        
        //toJson(ImmutableMap.<String, String> builder().put("itemLocation", "SanJose, CA").put("postalCode", "95125").build())

        given().log().all().
                auth().form("test_user", "password", config).
                contentType("application/json").header(new Header("Accept", "application/json")).
                multiPart("picture", new File("/home/shekhar/Desktop/all-folders/samsung-galaxy-note_1.jpg")).
                body(liberecoListingJson).
                expect().
                statusCode(201).
                log().all().
                post("/libereco/liberecolistings");
    }

}
