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

import java.util.UUID;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mortbay.jetty.Server;

public class LiberecoIntegrationTestSuite {

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
        shouldCreateUser();
        shouldReadUser();
        shouldNotFindUserWhichDoesNotExist();
        shouldReadAllUsers();
        shouldUpdateUser();
        shouldDeleteUser();
    }

    @Test
    public void shouldDoCRUDOperationsOnMarketplace() {
        shouldCreateMarketplace();
        shouldReadMarketplace();
        shouldNotFindMarketplaceWhichDoesNotExist();
        shouldReadAllMarketplaces();
        shouldUpdateMarketplace();
        shouldDeleteMarketplace();
    }

    @Test
    public void shouldDoCRUDOperationsOnLiberecoPaymentInformation() throws Exception {
        shouldCreateUser();
        shouldCreateMarketplace();

        shouldCreatePaymentInformation();
        shouldReadPaymentInformation();
        shouldNotFindPaymentInformationWhichDoesNotExist();
        shouldReadAllPaymentInformation();
        shouldUpdatePaymentInformation();
        shouldDeletePaymentInformation();
    }

    @Test
    public void shouldDoCRUDOperationsOnLiberecoShippingInformation() throws Exception {
        shouldCreateUser();
        shouldCreateMarketplace();

        shouldCreateShippingInformation();
        shouldReadShippingInformation();
        shouldReadAllShippingInformation();
        shouldUpdateShippingInformation();
        shouldDeleteShippingInformation();
    }
    
    @Test
    public void shouldAuthenticateWithMarketplaces() throws Exception {
        shouldCreateUser();
        shouldCreateMarketplace();
        shouldAutheticateWithEbay();
    }

    @Test
    public void shouldDoCRUDOperationsOnLiberecoListing() throws Exception {
        shouldCreateUser();
        shouldCreateMarketplace();
        shouldAutheticateWithEbay();

        String listingName = "Test Listing " + UUID.randomUUID().toString();
        shouldCreateLiberecoListing(listingName);
        shouldReadLiberecoListing(listingName);
        shouldReadAllLiberecoListing(listingName);
        shouldUpdateLiberecoListing(listingName);
        shouldDeleteLiberecoListing();
    }
    
    @Test
    public void shouldDoCRUDOperationsOnEbayListing() throws Exception {
        shouldCreateUser();
        shouldCreateMarketplace();
        shouldAutheticateWithEbay();

        String listingName = "Test Listing " + UUID.randomUUID().toString();
        shouldCreateEbayListing(listingName);
        shouldReadEbayListing();
        shouldReadAllEbayListing();
        shouldUpdateEbayListing(listingName);
        shouldDeleteEbayListing();
    }
}
