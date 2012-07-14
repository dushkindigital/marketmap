package com.libereco.integration.tests;

import static com.jayway.restassured.RestAssured.given;
import static com.libereco.integration.tests.JsonUtils.toJson;
import static com.libereco.integration.tests.TestDataUtils.shouldAutheticateWithEbay;
import static com.libereco.integration.tests.TestDataUtils.shouldCreateEbayListing;
import static com.libereco.integration.tests.TestDataUtils.shouldCreateLiberecoListing;
import static com.libereco.integration.tests.TestDataUtils.shouldCreateMarketplace;
import static com.libereco.integration.tests.TestDataUtils.shouldCreatePaymentInformation;
import static com.libereco.integration.tests.TestDataUtils.shouldCreateShippingInformation;
import static com.libereco.integration.tests.TestDataUtils.shouldCreateUser;
import static com.libereco.integration.tests.TestDataUtils.shouldDeleteEbayListing;
import static com.libereco.integration.tests.TestDataUtils.shouldDeleteLiberecoListing;
import static com.libereco.integration.tests.TestDataUtils.shouldDeleteMarketplace;
import static com.libereco.integration.tests.TestDataUtils.shouldDeletePaymentInformation;
import static com.libereco.integration.tests.TestDataUtils.shouldDeleteShippingInformation;
import static com.libereco.integration.tests.TestDataUtils.shouldDeleteUser;
import static com.libereco.integration.tests.TestDataUtils.shouldFetchToken;
import static com.libereco.integration.tests.TestDataUtils.shouldGetSessionId;
import static com.libereco.integration.tests.TestDataUtils.shouldNotFindMarketplaceWhichDoesNotExist;
import static com.libereco.integration.tests.TestDataUtils.shouldNotFindPaymentInformationWhichDoesNotExist;
import static com.libereco.integration.tests.TestDataUtils.shouldNotFindUserWhichDoesNotExist;
import static com.libereco.integration.tests.TestDataUtils.shouldReadAllEbayListing;
import static com.libereco.integration.tests.TestDataUtils.shouldReadAllLiberecoListing;
import static com.libereco.integration.tests.TestDataUtils.shouldReadAllMarketplaces;
import static com.libereco.integration.tests.TestDataUtils.shouldReadAllPaymentInformation;
import static com.libereco.integration.tests.TestDataUtils.shouldReadAllShippingInformation;
import static com.libereco.integration.tests.TestDataUtils.shouldReadAllUsers;
import static com.libereco.integration.tests.TestDataUtils.shouldReadEbayListing;
import static com.libereco.integration.tests.TestDataUtils.shouldReadLiberecoListing;
import static com.libereco.integration.tests.TestDataUtils.shouldReadMarketplace;
import static com.libereco.integration.tests.TestDataUtils.shouldReadPaymentInformation;
import static com.libereco.integration.tests.TestDataUtils.shouldReadShippingInformation;
import static com.libereco.integration.tests.TestDataUtils.shouldReadUser;
import static com.libereco.integration.tests.TestDataUtils.shouldUpdateEbayListing;
import static com.libereco.integration.tests.TestDataUtils.shouldUpdateLiberecoListing;
import static com.libereco.integration.tests.TestDataUtils.shouldUpdateMarketplace;
import static com.libereco.integration.tests.TestDataUtils.shouldUpdatePaymentInformation;
import static com.libereco.integration.tests.TestDataUtils.shouldUpdateShippingInformation;
import static com.libereco.integration.tests.TestDataUtils.shouldUpdateUser;
import static com.libereco.integration.tests.TestDataUtils.signinToEbay;

import java.util.List;
import java.util.UUID;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mortbay.jetty.Server;

import com.google.common.collect.ImmutableMap;
import com.jayway.restassured.authentication.FormAuthConfig;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Header;

public class LiberecoRestServiceIntegrationTest {

    private static Server server;

    @BeforeClass
    public static void startJettyServer() throws Exception {
        server = ServerUtils.startServer(8080, "/libereco", "../web/target/libereco.war");
    }

    @AfterClass
    public static void shutdownServer() throws Exception {
        server.stop();
    }

    @Test
    public void shouldDoCRUDOperationsOnLiberecoUser() {
        String userId = shouldCreateUser();
        shouldReadUser(userId);
        shouldNotFindUserWhichDoesNotExist();
        shouldReadAllUsers();
        shouldUpdateUser(userId);
        shouldDeleteUser(userId);
    }

    @Test
    public void shouldDoCRUDOperationsOnMarketplace() {
        String marketplaceId = shouldCreateMarketplace();
        shouldReadMarketplace(marketplaceId);
        shouldNotFindMarketplaceWhichDoesNotExist();
        shouldReadAllMarketplaces();
        shouldUpdateMarketplace(marketplaceId);
        shouldDeleteMarketplace(marketplaceId);

    }

    @Test
    public void shouldDoCRUDOperationsOnLiberecoPaymentInformation() throws Exception {
        String userId = shouldCreateUser();
        String marketplaceId = shouldCreateMarketplace();

        shouldCreatePaymentInformation();
        shouldReadPaymentInformation();
        shouldNotFindPaymentInformationWhichDoesNotExist();
        shouldReadAllPaymentInformation();
        shouldUpdatePaymentInformation();
        shouldDeletePaymentInformation();

        shouldDeleteMarketplace(marketplaceId);
        shouldDeleteUser(userId);
    }

    @Test
    public void shouldDoCRUDOperationsOnLiberecoShippingInformation() throws Exception {
        String userId = shouldCreateUser();
        String marketplaceId = shouldCreateMarketplace();

        shouldCreateShippingInformation();
        shouldReadShippingInformation();
        shouldReadAllShippingInformation();
        shouldUpdateShippingInformation();
        shouldDeleteShippingInformation();

        shouldDeleteMarketplace(marketplaceId);
        shouldDeleteUser(userId);
    }

    @Test
    public void shouldAuthenticateWithMarketplaces() throws Exception {
        String userId = shouldCreateUser();
        String marketplaceId = shouldCreateMarketplace();
        shouldAutheticateWithEbay();

        shouldDeleteMarketplace(marketplaceId);
        shouldDeleteUser(userId);
    }

    @Test
    public void shouldDoCRUDOperationsOnLiberecoListing() throws Exception {
        String userId = shouldCreateUser();
        String marketplaceId = shouldCreateMarketplace();
        shouldAutheticateWithEbay();

        String listingName = "Test Listing " + UUID.randomUUID().toString();
        String json = shouldCreateLiberecoListing(listingName, userId);
        JsonPath jsonPath = new JsonPath(json);
        int liberecoListingId = jsonPath.getInt("id");
        int liberecoListingVersionId = jsonPath.getInt("version");
        List<Integer> liberecoPaymentInformationIds = jsonPath.get("liberecoPaymentInformations.id");
        List<Integer> liberecoPaymentInformationVersions = jsonPath.get("liberecoPaymentInformations.version");
        List<Integer> liberecoShippingInformationIds = jsonPath.get("shippingInformations.id");
        List<Integer> liberecoShippingInformationVersions = jsonPath.get("shippingInformations.version");
        shouldReadLiberecoListing(listingName, userId, String.valueOf(liberecoListingId));

        shouldReadAllLiberecoListing(listingName, userId, String.valueOf(liberecoListingId));

        shouldUpdateLiberecoListing(listingName, userId, liberecoListingId, liberecoListingVersionId, liberecoShippingInformationIds.get(0),
                liberecoShippingInformationVersions.get(0), liberecoPaymentInformationIds.get(0), liberecoPaymentInformationVersions.get(0));
        shouldDeleteLiberecoListing(String.valueOf(liberecoListingId));

        shouldDeleteMarketplace(marketplaceId);
        shouldDeleteUser(userId);
    }

    @Test
    public void shouldDoCRUDOperationsOnEbayListing() throws Exception {
        String userId = shouldCreateUser();
        String marketplaceId = shouldCreateMarketplace();
        shouldAutheticateWithEbay();

        String listingName = "Test Listing " + UUID.randomUUID().toString();
        String json = shouldCreateLiberecoListing(listingName, userId);
        JsonPath jsonPath = new JsonPath(json);
        String liberecoListingId = jsonPath.getString("id");
        String liberecoListingVersionId = jsonPath.getString("version");

        shouldCreateEbayListing(listingName, liberecoListingId, liberecoListingVersionId);
        shouldReadEbayListing();
        shouldReadAllEbayListing();
        shouldUpdateEbayListing(listingName, liberecoListingId, liberecoListingVersionId);
        shouldDeleteEbayListing();

        shouldDeleteMarketplace(marketplaceId);
        shouldDeleteUser(userId);
    }

    @Test
    public void shouldNotGiveExceptionIfFetchTokenIsCalledMoreThanOnce_Issue_38() throws Exception {

        String userId = shouldCreateUser();
        String marketplaceId = shouldCreateMarketplace();

        String ebaySigninUrl = shouldGetSessionId();

        // PROGRAMMATICALLY SIGNIN TO EBAY

        signinToEbay(ebaySigninUrl);

        shouldFetchToken();

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

        shouldDeleteMarketplace(marketplaceId);
        shouldDeleteUser(userId);
    }

    @Test
    public void shouldNotThrowNullPointerExceptionWhenLiberecoListingIsCreatedWithoutMarketplaceAuthorization() throws Exception {
        String userId = shouldCreateUser();
        String marketplaceId = shouldCreateMarketplace();

        String listingName = "Test Listing " + UUID.randomUUID().toString();
        shouldCreateLiberecoListing(listingName, userId);

        shouldDeleteMarketplace(marketplaceId);
        shouldDeleteUser(userId);
    }

    @Test
    public void shouldGetErrorResponseWhenEbayingIsCreatedWithoutMarketplaceAuthorization() throws Exception {

        String userId = shouldCreateUser();
        String marketplaceId = shouldCreateMarketplace();

        String listingName = "Test Listing " + UUID.randomUUID().toString();
        String json = shouldCreateLiberecoListing(listingName, userId);
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
                auth().form("test_user", "password", config).
                contentType("application/json").header(new Header("Accept", "application/json")).
                body(ebayListingJson).
                expect().
                statusCode(404).
                log().all().
                post("/libereco/ebaylistings");
        
        
        shouldDeleteMarketplace(marketplaceId);
        shouldDeleteUser(userId);
    }
}
