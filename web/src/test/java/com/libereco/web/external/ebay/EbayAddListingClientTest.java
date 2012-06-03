package com.libereco.web.external.ebay;

import static org.junit.Assert.assertNotNull;

import java.util.UUID;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ebay.sdk.ApiContext;
import com.ebay.sdk.call.AddFixedPriceItemCall;
import com.ebay.soap.eBLBaseComponents.AmountType;
import com.ebay.soap.eBLBaseComponents.BuyerPaymentMethodCodeType;
import com.ebay.soap.eBLBaseComponents.CategoryType;
import com.ebay.soap.eBLBaseComponents.CountryCodeType;
import com.ebay.soap.eBLBaseComponents.CurrencyCodeType;
import com.ebay.soap.eBLBaseComponents.ErrorHandlingCodeType;
import com.ebay.soap.eBLBaseComponents.FeesType;
import com.ebay.soap.eBLBaseComponents.ItemType;
import com.ebay.soap.eBLBaseComponents.ListingTypeCodeType;
import com.ebay.soap.eBLBaseComponents.ProductListingDetailsType;
import com.ebay.soap.eBLBaseComponents.ReturnPolicyType;
import com.ebay.soap.eBLBaseComponents.ShippingDetailsType;
import com.ebay.soap.eBLBaseComponents.ShippingServiceOptionsType;
import com.ebay.soap.eBLBaseComponents.ShippingTypeCodeType;
import com.ebay.soap.eBLBaseComponents.WarningLevelCodeType;
import com.libereco.core.domain.EbayListing;
import com.libereco.core.domain.LiberecoCategory;
import com.libereco.core.domain.LiberecoListing;
import com.libereco.core.domain.ListingCondition;
import com.libereco.core.domain.ListingDuration;
import com.libereco.core.domain.ListingState;
import com.libereco.core.domain.ReturnPolicy;

@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext-web.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("test")
public class EbayAddListingClientTest {

    private static final String TOKEN = "AgAAAA**AQAAAA**aAAAAA**u5LJTw**nY+sHZ2PrBmdj6wVnY+sEZ2PrA2dj6wFk4GhCJiKpQ6dj6x9nY+seQ**5dEBAA**AAMAAA**K3YLHsnW2N67j6uI2leG4Ear6Z67/0ktEDFc439VTfKE/nB8uzBEBD9H7pwexITBtFgSp5InGJyYnCIx1S+KblwvVD+qrvU5pEFviGlKhf1EwaGy3ha2X1GB+bmBELGbN9h3PdeS5JRZqVgAsRM8KM4mjTy0ojYJ6F0uSz7v8lPAmo5PXkKB72PNWBqPJDNfklf8zc3isSsOFvqYDlFY7iZ36W01wR2AVf0AXmWyM3aphtAVfMSGHzXayPqw3djEq96upt8P6mRrcu8Gj1JKTyHwg1gT6wJs6X/Hu4R4506wvlDZ1ijB0Hi7NxBDh4VdmhRDe6G2GFJjkisHcwkbpKR0dFhZxTOwL3JtG+CQ5IB/tc8g0Ns37FAYj1K5Zjtlwya9wVYhiTPJTa9LmzhsSO0zvwr6zpVm/H5ULGSdVbxx0R2bNV2nGj2zOxIMotvwcwvXyXMfXxG1ouIIXnSIMHIFopE8GXsghj12njvg+evLXXYCB95GlMQHBxsjDkJsHljixaZZbQPDSKt77NxonpUACTNZN/CqDDzF1Dig+Iy61r3SD+3p//wXCF8u2yXMZ64XIYhMsxL2EHUyPmfJh/dvwxOZOSaIg3srm4SewhhQmK6wMSYhZX3g12xQf1JajBJot3eTKbbnPTwO67LlcaLzwl6mhLyIPpE7d2hiKOjixJKX2U2Y5HWyEfG9UU0i+ABiwQAFyqbqQI9PHHofkDBUyE/bvBH0CBOixeyqFmyflKEgANPLFLCOFt6f+PnT";

    @Autowired
    private EbayAddListingClient ebayAddListingClient;
    
    @Autowired
    private ApiContext apiContext;

    @Test
    public void shouldAddEbayListing() {
        EbayListing ebayListing = newEbayListing();
        ebayListing = ebayAddListingClient.addListing(ebayListing, TOKEN);
        assertNotNull(ebayListing.getEbayItemUrl());
    }

    
    @Test
    public void shouldAddEbayListingWithImage() {
        EbayListing ebayListing = newEbayListing();
        ebayListing.getLiberecoListing().setPictureUrl("http://resources.infosecinstitute.com/wp-content/uploads/iphone.jpg");
        ebayListing = ebayAddListingClient.addListing(ebayListing, TOKEN);
        assertNotNull(ebayListing.getEbayItemUrl());
        System.out.println(ebayListing.getEbayItemUrl());
    }

    @Test
    public void shouldAddEbayBuyItNowListingWithImage() {
        EbayListing ebayListing = newEbayBuyItNowListing();
        ebayListing.getLiberecoListing().setPictureUrl("http://resources.infosecinstitute.com/wp-content/uploads/iphone.jpg");
        ebayListing = ebayAddListingClient.addListing(ebayListing, TOKEN);
        assertNotNull(ebayListing.getEbayItemUrl());
        System.out.println(ebayListing.getEbayItemUrl());
    }
    
    @Ignore
    @Test
    public void testAddFixedPriceItemListing() throws Exception{
        apiContext.getApiCredential().seteBayToken(TOKEN);
        AddFixedPriceItemCall call = new AddFixedPriceItemCall(apiContext);
        call.setErrorHandling(ErrorHandlingCodeType.BEST_EFFORT);
        call.setWarningLevel(WarningLevelCodeType.HIGH);
        ItemType item =new ItemType();
        item.setTitle("Apple MacBook Pro MB990LL/A 13.3 in. Notebook");
        item.setDescription("Brand New Apple MacBook Pro MB990LL/A 13.3 in. Notebook!");
        CategoryType primaryCategory = new CategoryType();
        primaryCategory.setCategoryID("111422");
        item.setPrimaryCategory(primaryCategory);
        item.setStartPrice(toAmountType(500.0d));
        item.setCategoryMappingAllowed(true);
        item.setConditionID(1000);
        item.setCountry(CountryCodeType.US);
        item.setCurrency(CurrencyCodeType.USD);
        item.setDispatchTimeMax(3);
        item.setListingDuration("Days_7");
        item.setListingType(ListingTypeCodeType.FIXED_PRICE_ITEM);
        BuyerPaymentMethodCodeType[] methodTypes = {BuyerPaymentMethodCodeType.PAY_PAL};
        item.setPaymentMethods(methodTypes);
        item.setPayPalEmailAddress("test@gmail.com");
        item.setPostalCode("95125");
        ProductListingDetailsType productListingDetailsType = null;
        item.setProductListingDetails(productListingDetailsType);
        item.setQuantity(6);
        ReturnPolicyType returnPolicyType = new ReturnPolicyType();
        returnPolicyType.setRefundOption("MoneyBack");
        returnPolicyType.setReturnsAcceptedOption("ReturnsAccepted");
        returnPolicyType.setReturnsWithinOption("Days_30");
        returnPolicyType.setDescription("If you are not satisfied, return the item for refund.");
        returnPolicyType.setShippingCostPaidByOption("Buyer");
        item.setReturnPolicy(returnPolicyType);
        
        ShippingDetailsType shippingDetails = new ShippingDetailsType();
        shippingDetails.setShippingType(ShippingTypeCodeType.FLAT);

        ShippingServiceOptionsType shippingServiceOption = new ShippingServiceOptionsType();
        shippingServiceOption.setShippingService("USPSMedia");
        AmountType at = new AmountType();
        at.setValue(2.50);
        shippingServiceOption.setShippingServiceCost(at);
        
        ShippingServiceOptionsType[] shippingServiceOptions = { shippingServiceOption };
        shippingDetails.setShippingServiceOptions(shippingServiceOptions);
        item.setShippingDetails(shippingDetails);
        
        call.setItem(item);
        
        FeesType feesType = call.addFixedPriceItem();
    }

    private AmountType toAmountType(double amount) {
        AmountType amountType = new AmountType();
        amountType.setValue(amount);
        amountType.setCurrencyID(CurrencyCodeType.USD);
        return amountType;
    }


    private EbayListing newEbayBuyItNowListing() {
        EbayListing ebayListing = new EbayListing();
        ebayListing.setBestOfferEnabled(false);
        ebayListing.setBoldTitleChecked(false);
        ebayListing.setBorderChecked(false);
        ebayListing.setBuyItNowPrice(Double.valueOf(10.0d));
        ebayListing.setDispatchTimeMax(1);
        ebayListing.setLotSize(Integer.valueOf(1));
        ebayListing.setPaypalEmail("test@gmail.com");
        // ebayListing.setReservePrice(Double.valueOf(90.0d));
        ebayListing.setReturnPolicy(ReturnPolicy.SIXTY_DAY_RETURN);
        ebayListing.setAutoPay(false);
//        ebayListing.setStartPrice(0.00);
        ebayListing.setVatPercent(Float.valueOf(10.0f));
        ebayListing.setListingDuration(ListingDuration.DAYS_7);

        LiberecoListing liberecoListing = new LiberecoListing();
        liberecoListing.setCategory(LiberecoCategory.CAT_COMPUTER_OFFICE);
        liberecoListing.setDescription("Description");
        liberecoListing.setListingCondition(ListingCondition.FAIR);
        liberecoListing.setListingState(ListingState.NEW);
        liberecoListing.setName("RR11 Test Mobile" + UUID.randomUUID().toString());
        liberecoListing.setPrice(100.00);
        liberecoListing.setQuantity(1);
        liberecoListing.setUserId(Long.valueOf(1));

        ebayListing.setLiberecoListing(liberecoListing);
        return ebayListing;
    }
    
    private EbayListing newEbayListing() {
        EbayListing ebayListing = new EbayListing();
        ebayListing.setBestOfferEnabled(false);
        ebayListing.setBoldTitleChecked(false);
        ebayListing.setBorderChecked(false);
        ebayListing.setBuyItNowPrice(Double.valueOf(0.0d));
        ebayListing.setDispatchTimeMax(1);
        ebayListing.setLotSize(Integer.valueOf(1));
        ebayListing.setPaypalEmail("test@gmail.com");
        // ebayListing.setReservePrice(Double.valueOf(90.0d));
        ebayListing.setReturnPolicy(ReturnPolicy.SIXTY_DAY_RETURN);
        ebayListing.setAutoPay(false);
        ebayListing.setStartPrice(1.00);
        ebayListing.setVatPercent(Float.valueOf(10.0f));
        ebayListing.setListingDuration(ListingDuration.DAYS_7);

        LiberecoListing liberecoListing = new LiberecoListing();
        liberecoListing.setCategory(LiberecoCategory.CAT_COMPUTER_OFFICE);
        liberecoListing.setDescription("Description");
        liberecoListing.setListingCondition(ListingCondition.FAIR);
        liberecoListing.setListingState(ListingState.NEW);
        liberecoListing.setName("RR11 Test Mobile" + UUID.randomUUID().toString());
        liberecoListing.setPrice(1.00);
        liberecoListing.setQuantity(1);
        liberecoListing.setUserId(Long.valueOf(1));

        ebayListing.setLiberecoListing(liberecoListing);
        return ebayListing;
    }

}
