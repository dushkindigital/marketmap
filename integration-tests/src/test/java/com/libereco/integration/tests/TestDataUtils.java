package com.libereco.integration.tests;

import static com.jayway.restassured.RestAssured.given;
import static com.libereco.integration.tests.JsonUtils.toJson;
import static com.libereco.integration.tests.JsonUtils.toJsonObject;
import static com.libereco.integration.tests.JsonUtils.userJsonWithId;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jayway.restassured.authentication.FormAuthConfig;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Header;

public abstract class TestDataUtils {

    public static String shouldCreateUser() {
        return shouldCreateUser("test_user", "password");
    }

    public static String shouldCreateUser(String username, String password) {
        String userJson = toJson(ImmutableMap.<String, String> builder().put("username", username).put("password", password)
                .put("status", "ACTIVE").put("lastUpdated", new Date().toString()).build());

        String json = given().log().all().
                contentType("application/json").header(new Header("Accept", "application/json")).
                body(userJson).
                expect().
                statusCode(201).
                log().all().
                post("/libereco/liberecousers").asString();

        return JsonUtils.toMap(json).get("id");
    }

    public static void shouldReadUser(String userId) {
        given().log().all().
                contentType("application/json").header(new Header("Accept", "application/json")).
                expect().
                statusCode(200).
                body("id", equalTo(1)).
                body("username", equalTo("test_user")).
                body("password", equalTo("password")).
                body("status", equalTo("ACTIVE")).
                log().all().
                when().get("/libereco/liberecousers/" + userId);
    }

    public static void shouldNotFindUserWhichDoesNotExist() {
        given().log().all().
                contentType("application/json").header(new Header("Accept", "application/json")).
                expect().
                statusCode(404).
                log().all().
                when().get("/libereco/liberecousers/12345");
    }

    public static void shouldReadAllUsers() {
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
        given().log().all().
                contentType("application/json").header(new Header("Accept", "application/json")).
                expect().
                statusCode(200).
                body("username", equalTo(Arrays.asList("test_user", "test_user_008", "test_user_009"))).
                body("password", equalTo(Arrays.asList("password", "password", "password"))).
                body("status", equalTo(Arrays.asList("ACTIVE", "ACTIVE", "ACTIVE"))).
                log().all().
                when().get("/libereco/liberecousers");
    }

    public static void shouldUpdateUser(String userId) {
        String userUpdateJson = userJsonWithId("test_user", "password", Long.valueOf(userId));
        given().log().all().
                contentType("application/json").header(new Header("Accept", "application/json")).
                body(userUpdateJson).
                expect().
                statusCode(200).
                log().all().
                put("/libereco/liberecousers");
    }

    public static void shouldDeleteUser(String userId) {
        given().log().all().
                contentType("application/json").header(new Header("Accept", "application/json")).
                expect().
                statusCode(200).
                log().all().
                when().delete("/libereco/liberecousers/" + userId);

        given().log().all().
                contentType("application/json").header(new Header("Accept", "application/json")).
                expect().
                statusCode(404).
                log().all().
                when().delete("/libereco/liberecousers/" + userId);
    }

    public static String shouldCreateMarketplace() {
        String marketplaceJson = toJson(ImmutableMap.<String, String> builder().put("marketplaceName", "ebay")
                .put("marketplaceShortName", "ebay").build());

        String json = given().log().all().
                contentType("application/json").header(new Header("Accept", "application/json")).
                body(marketplaceJson).
                expect().
                statusCode(201).
                log().all().
                post("/libereco/marketplaces").asString();
        return JsonUtils.toMap(json).get("id");
    }

    public static void shouldReadMarketplace(String marketplaceId) {
        given().log().all().
                contentType("application/json").header(new Header("Accept", "application/json")).
                expect().
                statusCode(200).
                body("id", equalTo(1)).
                body("marketplaceName", equalTo("ebay")).
                body("marketplaceShortName", equalTo("ebay")).
                log().all().
                when().get("/libereco/marketplaces/" + marketplaceId);

    }

    public static void shouldNotFindMarketplaceWhichDoesNotExist() {
        given().log().all().
                contentType("application/json").header(new Header("Accept", "application/json")).
                expect().
                statusCode(404).
                log().all().
                when().get("/libereco/marketplaces/12345");
    }

    public static void shouldReadAllMarketplaces() {
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
                body("marketplaceName", equalTo(Arrays.asList("ebay", "test_marketplace_1", "test_marketplace_2"))).
                body("marketplaceShortName",
                        equalTo(Arrays.asList("ebay", "test_marketplace_shortname_1", "test_marketplace_shortname_2"))).
                log().all().
                when().get("/libereco/marketplaces");
    }

    public static void shouldUpdateMarketplace(String marketplaceId) {
        String marketplaceUpdateJson = toJson(ImmutableMap.<String, String> builder().put("id", marketplaceId)
                .put("marketplaceName", "test_marketplace")
                .put("marketplaceShortName", "test_marketplace_shortname").put("version", "0").build());

        given().log().all().
                contentType("application/json").header(new Header("Accept", "application/json")).
                body(marketplaceUpdateJson).
                expect().
                statusCode(200).
                log().all().
                put("/libereco/marketplaces");
    }

    public static void shouldDeleteMarketplace(String marketplaceId) {
        given().log().all().
                contentType("application/json").header(new Header("Accept", "application/json")).
                expect().
                statusCode(200).
                log().all().
                when().delete("/libereco/marketplaces/" + marketplaceId);

        given().log().all().
                contentType("application/json").header(new Header("Accept", "application/json")).
                expect().
                statusCode(404).
                log().all().
                when().delete("/libereco/marketplaces/" + marketplaceId);
    }

    public static void shouldAutheticateWithEbay() throws Exception {
        String ebaySigninUrl = shouldGetSessionId();

        // PROGRAMMATICALLY SIGNIN TO EBAY

        signinToEbay(ebaySigninUrl);

        shouldFetchToken();
    }

    public static String shouldGetSessionId() {
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
        return ebaySigninUrl;
    }

    public static void shouldFetchToken() {
        FormAuthConfig config = new FormAuthConfig("/libereco/resources/j_spring_security_check", "j_username", "j_password");
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
    }

    public static String shouldCreateShippingInformation() {
        // CREATE SHIPPING_INFORMATION
        FormAuthConfig config = new FormAuthConfig("/libereco/resources/j_spring_security_check", "j_username", "j_password");

        String shippingInformationJson = toJson(ImmutableMap.<String, String> builder().put("shippingType", "FLAT")
                .put("shippingService", "USPSMedia").put("shippingCost", "2.5").build());

        String json = given().
                auth().form("test_user", "password", config).
                contentType("application/json").header(new Header("Accept", "application/json")).
                body(shippingInformationJson).
                expect().
                statusCode(201).
                log().all().
                post("/libereco/liberecolisting/shippinginformations").asString();

        return JsonUtils.toMap(json).get("id");
    }

    public static void shouldReadShippingInformation() {
        FormAuthConfig config = new FormAuthConfig("/libereco/resources/j_spring_security_check", "j_username", "j_password");
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
    }

    public static void shouldReadAllShippingInformation() {
        FormAuthConfig config = new FormAuthConfig("/libereco/resources/j_spring_security_check", "j_username", "j_password");
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
    }

    public static void shouldUpdateShippingInformation() {
        FormAuthConfig config = new FormAuthConfig("/libereco/resources/j_spring_security_check", "j_username", "j_password");
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
    }

    public static void shouldDeleteShippingInformation() {
        FormAuthConfig config = new FormAuthConfig("/libereco/resources/j_spring_security_check", "j_username", "j_password");
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

    public static String shouldCreatePaymentInformation() {
        // CREATE PAYMENT_INFORMATION
        FormAuthConfig config = new FormAuthConfig("/libereco/resources/j_spring_security_check", "j_username", "j_password");

        String paymentInformationJson = toJson(ImmutableMap.<String, String> builder().put("paymentMethod", "AM_EX").build());

        String json = given().
                auth().form("test_user", "password", config).
                contentType("application/json").header(new Header("Accept", "application/json")).
                body(paymentInformationJson).
                expect().
                statusCode(201).
                log().all().
                post("/libereco/liberecolisting/paymentinformations").asString();

        return JsonUtils.toMap(json).get("id");

    }

    public static void shouldReadPaymentInformation() {
        FormAuthConfig config = new FormAuthConfig("/libereco/resources/j_spring_security_check", "j_username", "j_password");
        given().log().all().
                auth().form("test_user", "password", config).
                contentType("application/json").header(new Header("Accept", "application/json")).
                expect().
                statusCode(200).
                body("id", equalTo(1)).
                body("paymentMethod", equalTo("AM_EX")).
                log().all().
                when().get("/libereco/liberecolisting/paymentinformations/1");
    }

    public static void shouldNotFindPaymentInformationWhichDoesNotExist() {
        FormAuthConfig config = new FormAuthConfig("/libereco/resources/j_spring_security_check", "j_username", "j_password");
        given().log().all().
                auth().form("test_user", "password", config).
                contentType("application/json").header(new Header("Accept", "application/json")).
                expect().
                statusCode(404).
                log().all().
                when().get("/libereco/liberecolisting/paymentinformations/12345");
    }

    public static void shouldReadAllPaymentInformation() {
        FormAuthConfig config = new FormAuthConfig("/libereco/resources/j_spring_security_check", "j_username", "j_password");
        given().log().all().
                auth().form("test_user", "password", config).
                contentType("application/json").header(new Header("Accept", "application/json")).
                expect().
                statusCode(200).
                body("id", equalTo(Arrays.asList(1))).
                body("paymentMethod", equalTo(Arrays.asList("AM_EX"))).
                log().all().
                when().get("/libereco/liberecolisting/paymentinformations");
    }

    public static void shouldUpdatePaymentInformation() {
        FormAuthConfig config = new FormAuthConfig("/libereco/resources/j_spring_security_check", "j_username", "j_password");
        String paymentInformationUpdateJson = toJson(ImmutableMap.<String, String> builder().put("id", "1").put("paymentMethod", "AM_EX")
                .put("version", "0").build());

        given().log().all().
                auth().form("test_user", "password", config).
                contentType("application/json").header(new Header("Accept", "application/json")).
                body(paymentInformationUpdateJson).
                expect().
                statusCode(200).
                log().all().
                put("/libereco/liberecolisting/paymentinformations");
    }

    public static void shouldDeletePaymentInformation() {
        FormAuthConfig config = new FormAuthConfig("/libereco/resources/j_spring_security_check", "j_username", "j_password");
        given().log().all().
                auth().form("test_user", "password", config).
                contentType("application/json").header(new Header("Accept", "application/json")).
                expect().
                statusCode(200).
                log().all().
                when().delete("/libereco/liberecolisting/paymentinformations/1");

        given().log().all().
                auth().form("test_user", "password", config).
                contentType("application/json").header(new Header("Accept", "application/json")).
                expect().
                statusCode(404).
                log().all().
                when().delete("/libereco/liberecolisting/paymentinformations/1");
    }

    public static void signinToEbay(String ebaySigninUrl) throws Exception {
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

    public static String shouldCreateLiberecoListing(String listingName, String userId) throws Exception {
        return shouldCreateLiberecoListing(listingName, userId, "test_user", "password");
    }

    public static String shouldCreateLiberecoListing(String listingName, String userId, String username, String password) throws Exception {

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

        String json = given().log().all().
                auth().form(username, password, config).
                multiPart("picture", new File("src/test/resources/samsung-galaxy.jpg")).
                multiPart("json", liberecoListingJson).
                expect().
                statusCode(201).
                log().all().
                post("/libereco/liberecolistings").asString();

        JsonPath jsonPath = new JsonPath(json);
        String liberecoListingId = jsonPath.getString("id");

        InputStream inputStream = given().log().all().
                auth().form("test_user", "password", config).
                expect().
                get("/libereco/liberecolistings/" + liberecoListingId + "/image/samsung-galaxy.jpg").asInputStream();

        assertNotNull(inputStream);

        return json;
    }

    public static void shouldReadLiberecoListing(String listingName, String userId, String liberecoListingId) {
        FormAuthConfig config = new FormAuthConfig("/libereco/resources/j_spring_security_check", "j_username", "j_password");
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
                body("userId", equalTo(Long.valueOf(userId).intValue())).
                log().all().
                when().get("/libereco/liberecolistings/1");
    }

    public static void shouldReadAllLiberecoListing(String listingName, String userId, String liberecoListingId) {
        FormAuthConfig config = new FormAuthConfig("/libereco/resources/j_spring_security_check", "j_username", "j_password");
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
                body("userId", equalTo(Arrays.asList(Long.valueOf(userId).intValue()))).
                log().all().
                when().get("/libereco/liberecolistings");
    }

    public static void shouldUpdateLiberecoListing(String listingName, String userId, int liberecoListingId, int version, int shippingId,
            int shippingVersion, int paymentId, int paymentVersion) {
        String itemLocationJson = toJson(ImmutableMap.<String, String> builder().put("itemLocation", "SanJose, CA").put("postalCode", "95125")
                .build());

        FormAuthConfig config = new FormAuthConfig("/libereco/resources/j_spring_security_check", "j_username", "j_password");
        JsonArray updateShippingInformationJsonArray = new JsonArray();
        updateShippingInformationJsonArray.add(toJsonObject(ImmutableMap.<String, String> builder().put("id", String.valueOf(shippingId))
                .put("shippingCost", "2.5")
                .put("shippingService", "USPSMedia").put("shippingType", "FLAT").put("version", String.valueOf(shippingVersion)).build()));

        String updatedShippingInformationJson = updateShippingInformationJsonArray.toString();

        JsonArray updatePaymentInformationJsonArray = new JsonArray();
        updatePaymentInformationJsonArray.add(toJsonObject(ImmutableMap.<String, String> builder().put("id", String.valueOf(paymentId))
                .put("paymentMethod", "AM_EX")
                .put("version", String.valueOf(paymentVersion)).build()));
        String updatePaymentInformationJson = updatePaymentInformationJsonArray.toString();

        String updateLiberecoListingJson = toJson(ImmutableMap.<String, String> builder().
                put("name", listingName).
                put("id", String.valueOf(liberecoListingId)).
                put("userId", userId).
                put("price", "1000").
                put("quantity", "10").
                put("description", "Samsung Galaxy S3 Listing").
                put("category", "CAT_ELECTRONICS").
                put("listingCondition", "NEW").
                put("itemLocation", itemLocationJson).
                put("shippingInformations", updatedShippingInformationJson).
                put("liberecoPaymentInformations", updatePaymentInformationJson).
                put("version", String.valueOf(version)).
                build());
        given().log().all().
                auth().form("test_user", "password", config).
                multiPart("picture", new File("src/test/resources/samsung-galaxy-s3.jpg")).
                multiPart("json", updateLiberecoListingJson).
                expect().
                statusCode(200).
                log().all().
                post("/libereco/liberecolistings/update");

        // READ UPDATED LIBERECO_LISTING

        given().log().all().
                auth().form("test_user", "password", config).
                contentType("application/json").header(new Header("Accept", "application/json")).
                expect().
                statusCode(200).
                body("name", equalTo(listingName)).
                body("quantity", equalTo(10)).
                body("description", equalTo("Samsung Galaxy S3 Listing")).
                body("category", equalTo("CAT_ELECTRONICS")).
                body("listingCondition", equalTo("NEW")).
                body("pictureName", equalTo("samsung-galaxy-s3.jpg")).
                body("pictureUrl", equalTo("http://localhost:8080/libereco/liberecolistings/1/image/samsung-galaxy-s3.jpg")).
                body("userId", equalTo(Long.valueOf(userId).intValue())).
                log().all().
                when().get("/libereco/liberecolistings/" + liberecoListingId);
    }

    public static void shouldDeleteLiberecoListing(String liberecoListingId) {
        FormAuthConfig config = new FormAuthConfig("/libereco/resources/j_spring_security_check", "j_username", "j_password");
        given().log().all().
                auth().form("test_user", "password", config).
                contentType("application/json").header(new Header("Accept", "application/json")).
                expect().
                statusCode(200).
                log().all().
                when().delete("/libereco/liberecolistings/" + liberecoListingId);

        given().log().all().
                auth().form("test_user", "password", config).
                contentType("application/json").header(new Header("Accept", "application/json")).
                expect().
                statusCode(404).
                log().all().
                when().delete("/libereco/liberecolistings/" + liberecoListingId);
    }

    public static String shouldCreateEbayListing(String listingName, String liberecoListingId, String liberecoListingVersion) {
        FormAuthConfig config = new FormAuthConfig("/libereco/resources/j_spring_security_check", "j_username", "j_password");
        String liberecoListingJson = toJson(ImmutableMap.<String, String> builder().
                put("name", listingName).put("id", liberecoListingId).put("version", liberecoListingVersion).
                build());

        String ebayListingJson = toJson(ImmutableMap.<String, String> builder().put("returnPolicy", "NO_RETURN").put("dispatchTimeMax", "3")
                .put("startPrice", "100").put("paypalEmail", "test@gmail.com")

                .put("lotSize", "1").put("listingDuration", "DAYS_3").put("liberecoListing", liberecoListingJson).build());
        String json = given().
                auth().form("test_user", "password", config).
                contentType("application/json").header(new Header("Accept", "application/json")).
                body(ebayListingJson).
                expect().
                statusCode(201).
                log().all().
                post("/libereco/ebaylistings").asString();

        return json;
    }

    public static void shouldReadEbayListing() {
        FormAuthConfig config = new FormAuthConfig("/libereco/resources/j_spring_security_check", "j_username", "j_password");
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
    }

    public static void shouldReadAllEbayListing() {
        FormAuthConfig config = new FormAuthConfig("/libereco/resources/j_spring_security_check", "j_username", "j_password");
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
    }

    public static void shouldUpdateEbayListing(String listingName, String liberecoListingId, String liberecoListingVersion) {
        FormAuthConfig config = new FormAuthConfig("/libereco/resources/j_spring_security_check", "j_username", "j_password");

        String liberecoListingJson = toJson(ImmutableMap.<String, String> builder().
                put("name", listingName).put("id", liberecoListingId).put("version", liberecoListingVersion).
                build());

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
    }

    public static void shouldDeleteEbayListing() {
        FormAuthConfig config = new FormAuthConfig("/libereco/resources/j_spring_security_check", "j_username", "j_password");
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
