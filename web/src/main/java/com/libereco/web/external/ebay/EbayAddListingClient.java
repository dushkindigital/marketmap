package com.libereco.web.external.ebay;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.ebay.sdk.ApiContext;
import com.ebay.sdk.call.AddFixedPriceItemCall;
import com.ebay.soap.eBLBaseComponents.AmountType;
import com.ebay.soap.eBLBaseComponents.BuyerPaymentMethodCodeType;
import com.ebay.soap.eBLBaseComponents.CategoryType;
import com.ebay.soap.eBLBaseComponents.CountryCodeType;
import com.ebay.soap.eBLBaseComponents.CurrencyCodeType;
import com.ebay.soap.eBLBaseComponents.DetailLevelCodeType;
import com.ebay.soap.eBLBaseComponents.FeesType;
import com.ebay.soap.eBLBaseComponents.GalleryTypeCodeType;
import com.ebay.soap.eBLBaseComponents.ItemType;
import com.ebay.soap.eBLBaseComponents.ListingTypeCodeType;
import com.ebay.soap.eBLBaseComponents.PictureDetailsType;
import com.ebay.soap.eBLBaseComponents.ReturnPolicyType;
import com.ebay.soap.eBLBaseComponents.ShippingDetailsType;
import com.ebay.soap.eBLBaseComponents.ShippingServiceOptionsType;
import com.ebay.soap.eBLBaseComponents.ShippingTypeCodeType;
import com.ebay.soap.eBLBaseComponents.SiteCodeType;
import com.libereco.core.domain.EbayListing;
import com.libereco.core.domain.LiberecoListing;
import com.libereco.core.domain.ReturnPolicy;

@Component
public class EbayAddListingClient {

    private ApiContext apiContext;
    private Environment environment;

    @Autowired
    public EbayAddListingClient(ApiContext apiContext, Environment environment) {
        this.apiContext = apiContext;
        this.environment = environment;
    }

    public EbayListing addListing(EbayListing ebayListing, String token) {

        try {
            this.apiContext
                    .getApiCredential()
                    .seteBayToken(token);
            AddFixedPriceItemCall addFixedPriceItemCall = new AddFixedPriceItemCall(apiContext);
            addFixedPriceItemCall.setAutoSetItemUUID(false);
            addFixedPriceItemCall.setSite(SiteCodeType.US);
            // Set detail level to retrieve item description.
            addFixedPriceItemCall.addDetailLevel(DetailLevelCodeType.ITEM_RETURN_DESCRIPTION);

            ItemType item = toItemType(ebayListing);

            if (ebayListing.getLiberecoListing().getPictureUrl() != null) {
                String[] pictureURLs = { ebayListing.getLiberecoListing().getPictureUrl() };
                PictureDetailsType pictureDetailsObj = new PictureDetailsType();
                pictureDetailsObj.setPictureURL(pictureURLs);
                // To specify a Gallery Image
                pictureDetailsObj.setGalleryType(GalleryTypeCodeType.GALLERY);
                item.setPictureDetails(pictureDetailsObj);
            }
            addFixedPriceItemCall.setItem(item);
            /**
             * Defines a single new item and lists it on a specified eBay site.
             * Also for Half.com. Returns the item ID for the new listing, and
             * returns fees the seller will incur for the listing (not including
             * the Final Value Fee, which cannot be calculated until the item is
             * sold).
             */
            FeesType fees = addFixedPriceItemCall.addFixedPriceItem();
            String itemId = item.getItemID();
            System.out.println("Item: " + itemId);
            String ebayItemUrl = environment.getProperty("libereco.ebay.item.url");
            ebayItemUrl += itemId;
            ebayListing.setEbayItemUrl(ebayItemUrl);
            return ebayListing;
        } catch (Exception e) {
            throw new RuntimeException("Not able to addListing ", e);
        }
    }

    /**
     * @param ebayListing
     * @return
     */
    private ItemType toItemType(EbayListing ebayListing) {
        ItemType item = new ItemType();
        LiberecoListing liberecoListing = ebayListing.getLiberecoListing();
        item.setTitle(liberecoListing.getName());
        item.setDescription(liberecoListing.getDescription());

        CategoryType cat = new CategoryType();
        cat.setCategoryID("139971");
        item.setPrimaryCategory(cat);

        AmountType startPrice = toAmountType(ebayListing.getStartPrice());
        item.setStartPrice(startPrice);
        
        item.setCategoryMappingAllowed(true);

        // 1000 - New
        item.setConditionID(1000);
        item.setCountry(CountryCodeType.US);
        item.setCurrency(CurrencyCodeType.USD);
        item.setDispatchTimeMax(ebayListing.getDispatchTimeMax());
        item.setListingDuration(ebayListing.getListingDuration().getName());
        item.setListingType(ListingTypeCodeType.FIXED_PRICE_ITEM);
        
        item.setRegionID("0");
        item.setLocation("San Jose, CA");
        item.setPostalCode("95125");
        item.setQuantity(liberecoListing.getQuantity());
        
        BuyerPaymentMethodCodeType[] arrPaymentMethods = { BuyerPaymentMethodCodeType.AM_EX };
        item.setPaymentMethods(arrPaymentMethods);

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

        item.setPayPalEmailAddress(ebayListing.getPaypalEmail());

        item.setReturnPolicy(toReturnPolicy(ebayListing.getReturnPolicy()));

        return item;
    }

    private ReturnPolicyType toReturnPolicy(ReturnPolicy returnPolicy) {
        ReturnPolicyType returnPolicyType = new ReturnPolicyType();
        returnPolicyType.setReturnsAcceptedOption("ReturnsAccepted");
        return returnPolicyType;
    }

    private AmountType toAmountType(double price) {
        AmountType amount = new AmountType();
        amount.setCurrencyID(CurrencyCodeType.USD);
        amount.setValue(price);
        return amount;
    }
}
