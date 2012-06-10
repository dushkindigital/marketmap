package com.libereco.core.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.libereco.core.domain.EbayListing;
import com.libereco.core.domain.ItemLocation;
import com.libereco.core.domain.LiberecoCategory;
import com.libereco.core.domain.LiberecoListing;
import com.libereco.core.domain.ListingCondition;
import com.libereco.core.domain.ListingDuration;
import com.libereco.core.domain.ListingState;
import com.libereco.core.domain.ReturnPolicy;
import com.libereco.core.domain.ShippingInformation;
import com.libereco.core.domain.ShippingType;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/META-INF/spring/applicationContext*.xml")
@Transactional
@ActiveProfiles(profiles = "test")
public class EbayListingRepositoryTest {

    @Autowired
    EbayListingRepository ebayListingRepository;

    @Autowired
    LiberecoListingRepository liberecoListingRepository;

    @Test
    public void testSaveEbayListing() {
        EbayListing ebayListing = newEbayListing(1L);
        EbayListing persistedEbayListing = ebayListingRepository.save(ebayListing);
        assertNotNull(persistedEbayListing.getId());
    }

    @Test
    public void shouldFindAllEbayListingByLiberecoListing_UserId() throws Exception {
        EbayListing ebayListing1 = newEbayListing(1L);
        EbayListing ebayListing2 = newEbayListing(1L);
        EbayListing ebayListing3 = newEbayListing(2L);
        ebayListingRepository.save(ebayListing1);
        ebayListingRepository.save(ebayListing2);
        ebayListingRepository.save(ebayListing3);

        List<EbayListing> user1Listings = ebayListingRepository.findAllEbayListingByLiberecoListing_UserId(1L);
        assertEquals(2, user1Listings.size());

        List<EbayListing> user2Listings = ebayListingRepository.findAllEbayListingByLiberecoListing_UserId(2L);
        assertEquals(1, user2Listings.size());
    }

    @Test
    public void shouldFindAllEbayListingByLiberecoListing_UserIdWithPaging() throws Exception {
        for (int i = 0; i < 20; i++) {
            EbayListing ebayListing1 = newEbayListing(1L);
            ebayListingRepository.save(ebayListing1);
        }

        Page<EbayListing> listings = ebayListingRepository.findAllEbayListingByLiberecoListing_UserId(1L, new PageRequest(0, 10));
        assertEquals(10, listings.getContent().size());
    }

    private EbayListing newEbayListing(Long userId) {
        EbayListing ebayListing = new EbayListing();
        ebayListing.setDispatchTimeMax(Integer.valueOf(3));
        ebayListing.setLotSize(Integer.valueOf(1));
        ebayListing.setPaypalEmail("test@gmail.com");
        ebayListing.setReturnPolicy(ReturnPolicy.SIXTY_DAY_RETURN);
        ebayListing.setStartPrice(Double.valueOf(60.0d));
        ebayListing.setListingDuration(ListingDuration.DAYS_3);

        LiberecoListing liberecoListing = new LiberecoListing();
        liberecoListing.setCategory(LiberecoCategory.CAT_COMPUTER_OFFICE);
        liberecoListing.setDescription("Test Item");
        liberecoListing.setListingCondition(ListingCondition.NEW);
        liberecoListing.setListingState(ListingState.NEW);
        liberecoListing.setName("test_item");
        liberecoListing.setPrice(Double.valueOf(100.0d));
        liberecoListing.setQuantity(1);
        liberecoListing.setUserId(userId);
        ItemLocation itemLocation = new ItemLocation("San Jose, CA", "95125");
        liberecoListing.setItemLocation(itemLocation);
        ShippingInformation shippingInformation = new ShippingInformation();
        shippingInformation.setShippingType(ShippingType.FLAT);
        shippingInformation.setShippingService("USPSMedia");
        shippingInformation.setShippingCost(2.50);
        liberecoListing.setShippingInformations(Arrays.asList(shippingInformation));
        liberecoListing = liberecoListingRepository.save(liberecoListing);
        ebayListing.setLiberecoListing(liberecoListing);
        return ebayListing;
    }

}
