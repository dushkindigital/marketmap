package com.libereco.web.external.ebay;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
import com.ebay.soap.eBLBaseComponents.DetailNameCodeType;
import com.ebay.soap.eBLBaseComponents.ErrorHandlingCodeType;
import com.ebay.soap.eBLBaseComponents.FeesType;
import com.ebay.soap.eBLBaseComponents.ItemType;
import com.ebay.soap.eBLBaseComponents.ListingTypeCodeType;
import com.ebay.soap.eBLBaseComponents.ProductListingDetailsType;
import com.ebay.soap.eBLBaseComponents.ReturnPolicyType;
import com.ebay.soap.eBLBaseComponents.ShippingDetailsType;
import com.ebay.soap.eBLBaseComponents.ShippingServiceDetailsType;
import com.ebay.soap.eBLBaseComponents.ShippingServiceOptionsType;
import com.ebay.soap.eBLBaseComponents.ShippingTypeCodeType;
import com.ebay.soap.eBLBaseComponents.WarningLevelCodeType;
import com.libereco.core.domain.DelistingReason;
import com.libereco.core.domain.EbayListing;
import com.libereco.core.domain.ItemLocation;
import com.libereco.core.domain.LiberecoCategory;
import com.libereco.core.domain.LiberecoListing;
import com.libereco.core.domain.LiberecoPaymentInformation;
import com.libereco.core.domain.LiberecoShippingInformation;
import com.libereco.core.domain.ListingCondition;
import com.libereco.core.domain.ListingDuration;
import com.libereco.core.domain.ListingState;
import com.libereco.core.domain.PaymentMethod;
import com.libereco.core.domain.ReturnPolicy;
import com.libereco.core.domain.ShippingService;
import com.libereco.core.domain.ShippingType;

@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext-web.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("test")
public class EbayClientTest {

    private static final String TOKEN = "AgAAAA**AQAAAA**aAAAAA**u5LJTw**nY+sHZ2PrBmdj6wVnY+sEZ2PrA2dj6wFk4GhCJiKpQ6dj6x9nY+seQ**5dEBAA**AAMAAA**K3YLHsnW2N67j6uI2leG4Ear6Z67/0ktEDFc439VTfKE/nB8uzBEBD9H7pwexITBtFgSp5InGJyYnCIx1S+KblwvVD+qrvU5pEFviGlKhf1EwaGy3ha2X1GB+bmBELGbN9h3PdeS5JRZqVgAsRM8KM4mjTy0ojYJ6F0uSz7v8lPAmo5PXkKB72PNWBqPJDNfklf8zc3isSsOFvqYDlFY7iZ36W01wR2AVf0AXmWyM3aphtAVfMSGHzXayPqw3djEq96upt8P6mRrcu8Gj1JKTyHwg1gT6wJs6X/Hu4R4506wvlDZ1ijB0Hi7NxBDh4VdmhRDe6G2GFJjkisHcwkbpKR0dFhZxTOwL3JtG+CQ5IB/tc8g0Ns37FAYj1K5Zjtlwya9wVYhiTPJTa9LmzhsSO0zvwr6zpVm/H5ULGSdVbxx0R2bNV2nGj2zOxIMotvwcwvXyXMfXxG1ouIIXnSIMHIFopE8GXsghj12njvg+evLXXYCB95GlMQHBxsjDkJsHljixaZZbQPDSKt77NxonpUACTNZN/CqDDzF1Dig+Iy61r3SD+3p//wXCF8u2yXMZ64XIYhMsxL2EHUyPmfJh/dvwxOZOSaIg3srm4SewhhQmK6wMSYhZX3g12xQf1JajBJot3eTKbbnPTwO67LlcaLzwl6mhLyIPpE7d2hiKOjixJKX2U2Y5HWyEfG9UU0i+ABiwQAFyqbqQI9PHHofkDBUyE/bvBH0CBOixeyqFmyflKEgANPLFLCOFt6f+PnT";

    @Autowired
    private EbayClient ebayClient;

    @Autowired
    private ApiContext apiContext;

    @Test
    public void shouldAddEbayListing() {
        EbayListing ebayListing = newEbayListing(ListingCondition.NEW, getUspsMediaShippingInformation());
        ebayListing = ebayClient.addListing(ebayListing, TOKEN);
        String ebayItemUrl = ebayListing.getEbayItemUrl();
        System.out.println(ebayItemUrl);
        assertNotNull(ebayItemUrl);
    }

    @Test
    public void shouldAddEbayListingWithImage() {
        EbayListing ebayListing = newEbayListing(ListingCondition.NEW, getUspsMediaShippingInformation());
        ebayListing.getLiberecoListing().setPictureUrl("http://resources.infosecinstitute.com/wp-content/uploads/iphone.jpg");
        ebayListing = ebayClient.addListing(ebayListing, TOKEN);
        assertNotNull(ebayListing.getEbayItemUrl());
        System.out.println(ebayListing.getEbayItemUrl());
    }

    @Test
    public void shouldUpdateEbayListing() {
        EbayListing ebayListing = newEbayListing(ListingCondition.NEW, getUspsMediaShippingInformation());
        ebayListing = ebayClient.addListing(ebayListing, TOKEN);
        String ebayItemUrl = ebayListing.getEbayItemUrl();
        System.out.println(ebayItemUrl);
        assertNotNull(ebayItemUrl);

        ebayListing.setDispatchTimeMax(10);
        ebayListing.setStartPrice(2.0);

        EbayListing revisedEbayListing = ebayClient.reviseListing(ebayListing, TOKEN);

        ebayItemUrl = revisedEbayListing.getEbayItemUrl();
        System.out.println(ebayItemUrl);
        assertNotNull(ebayItemUrl);
    }

    @Test
    public void shouldUpdateEbayListingWithImage() {
        EbayListing ebayListing = newEbayListing(ListingCondition.NEW, getUspsMediaShippingInformation());
        ebayListing.getLiberecoListing().setPictureUrl("http://resources.infosecinstitute.com/wp-content/uploads/iphone.jpg");
        ebayListing = ebayClient.addListing(ebayListing, TOKEN);
        assertNotNull(ebayListing.getEbayItemUrl());
        System.out.println(ebayListing.getEbayItemUrl());

        ebayListing.setDispatchTimeMax(10);
        ebayListing.setStartPrice(2.0);
        ebayListing.getLiberecoListing().setPictureUrl("http://1revolution.info/wp-content/uploads/2012/06/iphone-5.png");
        EbayListing revisedEbayListing = ebayClient.reviseListing(ebayListing, TOKEN);

        String ebayItemUrl = revisedEbayListing.getEbayItemUrl();
        System.out.println(ebayItemUrl);
        assertNotNull(ebayItemUrl);
    }

    @Test
    public void shouldDelistItemOnEbay() {
        EbayListing ebayListing = newEbayListing(ListingCondition.NEW, getUspsMediaShippingInformation());
//        ebayListing = ebayClient.addListing(ebayListing, TOKEN);
//        String ebayItemUrl = ebayListing.getEbayItemUrl();
//        System.out.println(ebayItemUrl);
//        assertNotNull(ebayItemUrl);
        ebayListing.setEbayItemId("110101313287");
        ebayClient.delistItem(ebayListing, TOKEN, DelistingReason.OTHER_REASON);
    }

    @Test
    public void shouldCreateEbayListingsWithAllListingConditions() {
        List<String> notCreated = new ArrayList<String>();
        List<String> created = new ArrayList<String>();
        ListingCondition[] listingConditions = ListingCondition.values();
        for (ListingCondition listingCondition : listingConditions) {
            try {
                EbayListing ebayListing = newEbayListing(listingCondition, getUspsMediaShippingInformation());
                ebayListing = ebayClient.addListing(ebayListing, TOKEN);
                assertNotNull(ebayListing.getEbayItemUrl());
                created.add("Created listing for " + listingCondition);
            } catch (Exception e) {
                notCreated.add("Not Able to create listing for " + listingCondition);
                notCreated.add(e.getCause().getMessage());
            }
        }

        System.out.println("**********************************************Created ***************************************************");
        for (String message : created) {
            System.out.println(message);
        }

        System.out.println("**********************************************Not Created ***************************************************");
        for (String message : notCreated) {
            System.out.println(message);
        }
    }

    @Test
    @Ignore
    public void shouldAddItemWithShippingInformationFedEx() {

        // String[] shippingServices = { "FedExHomeDelivery","USPSPriority",
        // "UPSGround", "UPSNextDay" };

        ShippingServiceDetailsType[] shippingServiceDetails = ebayClient.getEbayShippingServiceDetails(DetailNameCodeType.SHIPPING_SERVICE_DETAILS,
                TOKEN);

        List<String> successfulListing = new ArrayList<String>();
        List<String> failedListing = new ArrayList<String>();
        for (ShippingServiceDetailsType shippingService : shippingServiceDetails) {
            try {
                LiberecoShippingInformation shippingInformation = new LiberecoShippingInformation();
                shippingInformation.setShippingType(ShippingType.FLAT);
                String shippingServiceInfo = shippingService.getShippingService();
                shippingInformation.setShippingService(ShippingService.fromString(shippingServiceInfo));
                shippingInformation.setShippingCost(2.50);

                EbayListing ebayListing = newEbayListing(ListingCondition.NEW, shippingInformation);
                ebayListing = ebayClient.addListing(ebayListing, TOKEN);
                String ebayItemUrl = ebayListing.getEbayItemUrl();
                assertNotNull(ebayItemUrl);
                successfulListing.add("SuccessFully Created Listing for shipping service : " + shippingServiceInfo);
            } catch (Exception e) {
                failedListing.add("Not able to create listing " + e.getMessage());
                failedListing.add("Not able to create listing for " + shippingService.getShippingService());
            }
        }

        System.out.println("Successful Listings");
        for (String success : successfulListing) {
            System.out.println(success);
        }

        System.out.println("Failed Listings");
        for (String failed : failedListing) {
            System.out.println(failed);
        }
    }

    @Test
    public void shouldGetEbayDetails() {
        ShippingServiceDetailsType[] ebayShippingServiceDetails = ebayClient.getEbayShippingServiceDetails(
                DetailNameCodeType.SHIPPING_SERVICE_DETAILS, TOKEN);
        StringBuilder sb = new StringBuilder();
        for (ShippingServiceDetailsType shippingServiceDetailsType : ebayShippingServiceDetails) {
            sb.append(shippingServiceDetailsType.getShippingService()).append(",");
        }
        System.out.println(sb);
    }

    @Test
    public void shouldGetEbayCategories() {
        ebayClient.getEbayCategories(TOKEN);
    }
    
    @Ignore
    @Test
    public void testAddFixedPriceItemListing() throws Exception {
        apiContext.getApiCredential().seteBayToken(TOKEN);
        AddFixedPriceItemCall call = new AddFixedPriceItemCall(apiContext);
        call.setErrorHandling(ErrorHandlingCodeType.BEST_EFFORT);
        call.setWarningLevel(WarningLevelCodeType.HIGH);
        ItemType item = new ItemType();
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
        BuyerPaymentMethodCodeType[] methodTypes = { BuyerPaymentMethodCodeType.PAY_PAL };
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

    private EbayListing newEbayListing(ListingCondition listingCondition, LiberecoShippingInformation shippingInformation) {
        EbayListing ebayListing = new EbayListing();
        ebayListing.setDispatchTimeMax(1);
        ebayListing.setLotSize(Integer.valueOf(1));
        ebayListing.setPaypalEmail("test@gmail.com");
        // ebayListing.setReservePrice(Double.valueOf(90.0d));
        ebayListing.setReturnPolicy(ReturnPolicy.SIXTY_DAY_RETURN);

        ebayListing.setStartPrice(1.00);
        ebayListing.setListingDuration(ListingDuration.DAYS_7);

        LiberecoListing liberecoListing = new LiberecoListing();
        liberecoListing.setCategory(LiberecoCategory.CAT_COMPUTER_OFFICE);
        liberecoListing.setDescription("Description");
        liberecoListing.setListingCondition(listingCondition);
        liberecoListing.setListingState(ListingState.NEW);
        liberecoListing.setName("RR11 Test Mobile" + UUID.randomUUID().toString());
        liberecoListing.setPrice(1.00);
        liberecoListing.setQuantity(1);
        liberecoListing.setUserId(Long.valueOf(1));
        ItemLocation itemLocation = new ItemLocation("San Jose, CA", "95125");
        liberecoListing.setItemLocation(itemLocation);
        LiberecoPaymentInformation paymentInformation = new LiberecoPaymentInformation();
        paymentInformation.setPaymentMethod(PaymentMethod.PAYPAL);
        liberecoListing.setLiberecoPaymentInformations(Arrays.asList(paymentInformation));

        liberecoListing.setShippingInformations(Arrays.asList(shippingInformation));

        ebayListing.setLiberecoListing(liberecoListing);
        return ebayListing;
    }

    public LiberecoShippingInformation getUspsMediaShippingInformation() {
        LiberecoShippingInformation shippingInformation = new LiberecoShippingInformation();
        shippingInformation.setShippingType(ShippingType.FLAT);
        shippingInformation.setShippingService(ShippingService.USPSMedia);
        shippingInformation.setShippingCost(2.50);
        return shippingInformation;
    }

}
