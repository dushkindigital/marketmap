package com.libereco.integration.tests;

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

import java.util.List;
import java.util.UUID;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mortbay.jetty.Server;

import com.jayway.restassured.path.json.JsonPath;

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

}
