package com.libereco.web.external.ebay;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.libereco.core.domain.EbayListing;
import com.libereco.core.domain.LiberecoCategory;
import com.libereco.core.domain.LiberecoListing;
import com.libereco.core.domain.ListingCondition;
import com.libereco.core.domain.ListingState;
import com.libereco.core.domain.ReturnPolicy;

@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext-web.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("test")
public class EbayAddListingClientTest {

    @Autowired
    private EbayAddListingClient ebayAddListingClient;
    
    @Test
    public void shouldAddEbayListing() {
        EbayListing ebayListing = newEbayListing();
        
        ebayAddListingClient.addListing(ebayListing);
    }
    
    private EbayListing newEbayListing(){
        EbayListing ebayListing = new EbayListing();
        ebayListing.setBestOfferEnabled(false);
        ebayListing.setBoldTitleChecked(false);
        ebayListing.setBorderChecked(false);
        ebayListing.setBuyItNowPrice(Double.valueOf(0.0d));
        ebayListing.setDispatchTimeMax(1);
        ebayListing.setLotSize(Integer.valueOf(1));
        ebayListing.setPaypalEmail("test@gmail.com");
//        ebayListing.setReservePrice(Double.valueOf(90.0d));
        ebayListing.setReturnPolicy(ReturnPolicy.SIXTY_DAY_RETURN);
        ebayListing.setAutoPay(false);
        ebayListing.setStartPrice(1.00);
        ebayListing.setVatPercent(Float.valueOf(10.0f));
        
        LiberecoListing liberecoListing = new LiberecoListing();
        liberecoListing.setCategory(LiberecoCategory.CAT_COMPUTER_OFFICE);
        liberecoListing.setDescription("Description");
        liberecoListing.setListingCondition(ListingCondition.FAIR);
        liberecoListing.setListingDuration(new Date());
        liberecoListing.setListingState(ListingState.NEW);
        liberecoListing.setName("RR11 Test Mobile1");
        liberecoListing.setPrice(1.00);
        liberecoListing.setQuantity(1);
        liberecoListing.setUserId(Long.valueOf(1));
        
        ebayListing.setLiberecoListing(liberecoListing);
        return ebayListing;
    }

}
