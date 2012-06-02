package com.libereco.core.repository;

import static org.junit.Assert.assertNotNull;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.libereco.core.domain.EbayListing;
import com.libereco.core.domain.LiberecoCategory;
import com.libereco.core.domain.LiberecoListing;
import com.libereco.core.domain.ListingCondition;
import com.libereco.core.domain.ListingState;
import com.libereco.core.domain.ReturnPolicy;

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
        EbayListing ebayListing = new EbayListing();
        ebayListing.setBestOfferEnabled(true);
        ebayListing.setBoldTitleChecked(true);
        ebayListing.setBorderChecked(true);
        ebayListing.setBuyItNowPrice(Double.valueOf(100.0d));
        ebayListing.setDispatchTimeMax(Integer.valueOf(3));
        ebayListing.setLotSize(Integer.valueOf(1));
        ebayListing.setPaypalEmail("test@gmail.com");
        ebayListing.setReservePrice(Double.valueOf(90.0d));
        ebayListing.setReturnPolicy(ReturnPolicy.SIXTY_DAY_RETURN);
        ebayListing.setSetAutoPay(true);
        ebayListing.setStartPrice(Double.valueOf(60.0d));
        ebayListing.setVatPercent(Float.valueOf(10.0f));
        
        LiberecoListing liberecoListing = new LiberecoListing();
        liberecoListing.setCategory(LiberecoCategory.CAT_COMPUTER_OFFICE);
        liberecoListing.setDescription("Test Item");
        liberecoListing.setListingCondition(ListingCondition.FAIR);
        liberecoListing.setListingDuration(new Date());
        liberecoListing.setListingState(ListingState.NEW);
        liberecoListing.setName("test_item");
        liberecoListing.setPrice(Double.valueOf(100.0d));
        liberecoListing.setQuantity(1);
        liberecoListing.setUserId(Long.valueOf(1));
        
        liberecoListing = liberecoListingRepository.save(liberecoListing);
        ebayListing.setLiberecoListing(liberecoListing);
        
        EbayListing persistedEbayListing = ebayListingRepository.save(ebayListing);
        assertNotNull(persistedEbayListing.getId());
    }
    
}
