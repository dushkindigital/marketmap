package com.libereco.core.repository;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.libereco.core.domain.LiberecoCategory;
import com.libereco.core.domain.LiberecoListing;
import com.libereco.core.domain.ListingCondition;
import com.libereco.core.domain.ListingState;
import com.libereco.core.domain.Marketplace;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/META-INF/spring/applicationContext*.xml")
@Transactional
@ActiveProfiles(profiles = "test")
public class LiberecoListingRepositoryTest {

    @Autowired
    LiberecoListingRepository liberecoListingRepository;

    @Autowired
    MarketplaceRepository marketplaceRepository;

    @Test
    public void shouldFindAllListingByUserId() throws Exception {
        Long userId = 1L;
        LiberecoListing liberecoListing1 = newLiberecoListing(userId);
        LiberecoListing liberecoListing2 = newLiberecoListing(2L);
        liberecoListingRepository.save(liberecoListing1);
        liberecoListingRepository.save(liberecoListing2);
        List<LiberecoListing> listingByUserId = liberecoListingRepository.findAllListingByUserId(userId);
        assertEquals(1, listingByUserId.size());
        listingByUserId = liberecoListingRepository.findAllListingByUserId(2L);
        assertEquals(1, listingByUserId.size());
    }

    @Test
    public void shouldFindAllListingByUserIdWithPageRequest() throws Exception {
        Long userId = 1L;
        for (int i = 0; i < 20; i++) {
            LiberecoListing liberecoListing = newLiberecoListing(userId);
            liberecoListingRepository.save(liberecoListing);
        }
        List<LiberecoListing> listingByUserId = liberecoListingRepository.findAllListingByUserId(userId);
        assertEquals(20, listingByUserId.size());
        listingByUserId = liberecoListingRepository.findListingsByUserId(userId, new PageRequest(0, 10)).getContent();
        assertEquals(10, listingByUserId.size());
    }

    @Test
    public void shouldFindAllListingsByUserIdAndMarketplaces_MarketplaceNameNot() {
        LiberecoListing liberecoListing = newLiberecoListing(1L);
        Set<Marketplace> marketplaces = new HashSet<Marketplace>();
        Marketplace marketplace = new Marketplace("ebay", "ebay");
        marketplaceRepository.save(marketplace);
        marketplaces.add(marketplace);
        liberecoListing.setMarketplaces(marketplaces);

        liberecoListingRepository.save(liberecoListing);

        List<LiberecoListing> listings = liberecoListingRepository.findAllListingsByUserIdAndMarketplaces_MarketplaceNameNot(1L, "ebay");

        assertEquals(0, listings.size());
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
        return liberecoListing;
    }

}
