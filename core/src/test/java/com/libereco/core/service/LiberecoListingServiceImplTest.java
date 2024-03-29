package com.libereco.core.service;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.libereco.core.domain.ItemLocation;
import com.libereco.core.domain.LiberecoCategory;
import com.libereco.core.domain.LiberecoListing;
import com.libereco.core.domain.LiberecoPaymentInformation;
import com.libereco.core.domain.ListingCondition;
import com.libereco.core.domain.ListingState;
import com.libereco.core.domain.Marketplace;
import com.libereco.core.domain.LiberecoShippingInformation;
import com.libereco.core.domain.PaymentMethod;
import com.libereco.core.domain.ShippingService;
import com.libereco.core.domain.ShippingType;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/META-INF/spring/applicationContext*.xml")
@Transactional
@ActiveProfiles(profiles = "test")
public class LiberecoListingServiceImplTest {

    @Autowired
    private LiberecoListingService liberecoListingService;
    @Autowired
    private MarketplaceService marketplaceService;

    @Test
    public void shouldFindListingsWhenListingIsNotAssociatedWithAnyMarketplace() {
        liberecoListingService.saveLiberecoListing(newLiberecoListing(1L));

        List<LiberecoListing> listings = liberecoListingService.findAllNotListedListingsForUser(1L, "ebay");
        assertEquals(1, listings.size());
    }

    @Test
    public void shouldNotFindListingWhenListingIsAlreadyAssociatedWithEbay() {
        LiberecoListing newLiberecoListing = newLiberecoListing(1L);
        Marketplace marketplace = new Marketplace("ebay", "ebay");
        marketplaceService.saveMarketplace(marketplace);
        newLiberecoListing.getMarketplaces().add(marketplace);
        liberecoListingService.saveLiberecoListing(newLiberecoListing);

        List<LiberecoListing> listings = liberecoListingService.findAllNotListedListingsForUser(1L, "ebay");
        assertEquals(0, listings.size());
    }

    @Test
    public void shouldFindListingWhenListingIsAssociatedWithMarketplaceButNotWithOneWeAreSearching() {
        LiberecoListing newLiberecoListing = newLiberecoListing(1L);
        Marketplace marketplace = new Marketplace("test", "test");
        marketplaceService.saveMarketplace(marketplace);
        newLiberecoListing.getMarketplaces().add(marketplace);
        liberecoListingService.saveLiberecoListing(newLiberecoListing);

        List<LiberecoListing> listings = liberecoListingService.findAllNotListedListingsForUser(1L, "ebay");
        assertEquals(1, listings.size());

    }

    private LiberecoListing newLiberecoListing(Long userId) {
        LiberecoListing liberecoListing = new LiberecoListing();
        liberecoListing.setCategory(LiberecoCategory.CAT_COMPUTER_OFFICE);
        liberecoListing.setDescription("Description");
        liberecoListing.setListingCondition(ListingCondition.NEW);
        liberecoListing.setListingState(ListingState.NEW);
        liberecoListing.setName("RR11 Test Mobile" + UUID.randomUUID().toString());
        liberecoListing.setPrice(1.00);
        liberecoListing.setQuantity(1);
        liberecoListing.setUserId(userId);
        ItemLocation itemLocation = new ItemLocation("San Jose, CA", "95125");
        liberecoListing.setItemLocation(itemLocation);
        LiberecoShippingInformation shippingInformation = new LiberecoShippingInformation();
        shippingInformation.setShippingType(ShippingType.FLAT);
        shippingInformation.setShippingService(ShippingService.USPSMedia);
        shippingInformation.setShippingCost(2.50);
        liberecoListing.setShippingInformations(Arrays.asList(shippingInformation));
        LiberecoPaymentInformation paymentInformation = new LiberecoPaymentInformation();
        paymentInformation.setPaymentMethod(PaymentMethod.PAYPAL);
        liberecoListing.setLiberecoPaymentInformations(Arrays.asList(paymentInformation));
        return liberecoListing;
    }

}
