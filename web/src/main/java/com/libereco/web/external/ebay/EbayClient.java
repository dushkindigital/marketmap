package com.libereco.web.external.ebay;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.ebay.sdk.ApiContext;
import com.ebay.sdk.call.AddFixedPriceItemCall;
import com.ebay.sdk.call.EndFixedPriceItemCall;
import com.ebay.sdk.call.ReviseFixedPriceItemCall;
import com.ebay.soap.eBLBaseComponents.AmountType;
import com.ebay.soap.eBLBaseComponents.BuyerPaymentMethodCodeType;
import com.ebay.soap.eBLBaseComponents.CategoryType;
import com.ebay.soap.eBLBaseComponents.CountryCodeType;
import com.ebay.soap.eBLBaseComponents.CurrencyCodeType;
import com.ebay.soap.eBLBaseComponents.DetailLevelCodeType;
import com.ebay.soap.eBLBaseComponents.EndReasonCodeType;
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
import com.libereco.core.domain.DelistingReason;
import com.libereco.core.domain.EbayListing;
import com.libereco.core.domain.LiberecoListing;
import com.libereco.core.domain.LiberecoPaymentInformation;
import com.libereco.core.domain.LiberecoShippingInformation;
import com.libereco.core.domain.ListingCondition;
import com.libereco.core.domain.PaymentMethod;
import com.libereco.core.domain.ReturnPolicy;
import com.libereco.core.domain.ShippingType;

@Component
public class EbayClient {

    private ApiContext apiContext;
    private Environment environment;

    @Autowired
    public EbayClient(ApiContext apiContext, Environment environment) {
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

            setPictureDetails(ebayListing, item);
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
            ebayListing.setEbayItemId(itemId);

            String ebayItemUrl = environment.getProperty("libereco.ebay.item.url");
            ebayItemUrl += itemId;
            ebayListing.setEbayItemUrl(ebayItemUrl);
            return ebayListing;
        } catch (Exception e) {
            String exceptionMessage = getExceptionMessage(e);
            throw new RuntimeException(exceptionMessage, e);
        }
    }

    public EbayListing reviseListing(EbayListing ebayListing, String token) {
        try {
            this.apiContext
                    .getApiCredential()
                    .seteBayToken(token);
            ReviseFixedPriceItemCall reviseFixedPriceItemCall = new ReviseFixedPriceItemCall(apiContext);
            reviseFixedPriceItemCall.setSite(SiteCodeType.US);
            // Set detail level to retrieve item description.
            reviseFixedPriceItemCall.addDetailLevel(DetailLevelCodeType.ITEM_RETURN_DESCRIPTION);
            ItemType item = toItemType(ebayListing);
            setPictureDetails(ebayListing, item);
            item.setItemID(ebayListing.getEbayItemId());
            reviseFixedPriceItemCall.setItemToBeRevised(item);
            FeesType fees = reviseFixedPriceItemCall.reviseFixedPriceItem();
            String itemId = item.getItemID();
            System.out.println("Item: " + itemId);
            ebayListing.setEbayItemId(itemId);
            String ebayItemUrl = environment.getProperty("libereco.ebay.item.url");
            ebayItemUrl += itemId;
            ebayListing.setEbayItemUrl(ebayItemUrl);
            return ebayListing;
        } catch (Exception e) {
            String exceptionMessage = getExceptionMessage(e);
            throw new RuntimeException(exceptionMessage, e);
        }
    }

    private void setPictureDetails(EbayListing ebayListing, ItemType item) {
        if (ebayListing.getLiberecoListing().getPictureUrl() != null) {
            String[] pictureURLs = { ebayListing.getLiberecoListing().getPictureUrl() };
            PictureDetailsType pictureDetailsObj = new PictureDetailsType();
            pictureDetailsObj.setPictureURL(pictureURLs);
            // To specify a Gallery Image
            pictureDetailsObj.setGalleryType(GalleryTypeCodeType.GALLERY);
            item.setPictureDetails(pictureDetailsObj);
        }
    }

    public void delistItem(EbayListing ebayListing, String token, DelistingReason reason) {
        EndFixedPriceItemCall endFixedPriceItemCall = new EndFixedPriceItemCall();
        endFixedPriceItemCall.setApiContext(apiContext);
        endFixedPriceItemCall.setEndingReason(toEbayEndingReason(reason));
        endFixedPriceItemCall.setItemID(ebayListing.getEbayItemId());
        try {
            endFixedPriceItemCall.endFixedPriceItem();
        } catch (Exception e) {
            String exceptionMessage = getExceptionMessage(e);
            throw new RuntimeException(exceptionMessage, e);
        }
    }

    private EndReasonCodeType toEbayEndingReason(DelistingReason reason) {
        switch (reason) {
        case INCORRECT_PRICE:
            return EndReasonCodeType.INCORRECT;
        case LOST_OR_BROKEN:
            return EndReasonCodeType.LOST_OR_BROKEN;
        case OTHER_REASON:
            return EndReasonCodeType.OTHER_LISTING_ERROR;
        case SOLD:
            return EndReasonCodeType.SOLD;
        }
        
        throw new IllegalArgumentException("No Ebay EndReasonCodeType found for delisting reason : " + reason);
    }

    private String getExceptionMessage(Exception e) {
        String message = e.getMessage();
        String cause = e.getCause() == null ? "" : e.getCause().getMessage();

        StringBuilder exceptionMessageBuilder = new StringBuilder("Not able to addListing ");
        exceptionMessageBuilder.append("\n").append(message).append(" \n Cause : ").append(cause);
        return exceptionMessageBuilder.toString();
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

        item.setConditionID(toConditionId(ebayListing.getLiberecoListing().getListingCondition()));
        item.setDispatchTimeMax(ebayListing.getDispatchTimeMax());
        item.setListingDuration(ebayListing.getListingDuration().getName());
        item.setListingType(ListingTypeCodeType.FIXED_PRICE_ITEM);

        // item.setRegionID("0");
        // item.setLocation("San Jose, CA");
        // item.setPostalCode("95125");
        item.setCurrency(CurrencyCodeType.USD);
        item.setCountry(CountryCodeType.US);
        item.setLocation(liberecoListing.getItemLocation().getItemLocation());
        item.setPostalCode(liberecoListing.getItemLocation().getPostalCode());

        item.setQuantity(liberecoListing.getQuantity());

        List<LiberecoPaymentInformation> liberecoPaymentInformations = liberecoListing.getLiberecoPaymentInformations();
        BuyerPaymentMethodCodeType[] arrPaymentMethods = new BuyerPaymentMethodCodeType[liberecoPaymentInformations.size()];

        for (int i = 0; i < liberecoPaymentInformations.size(); i++) {
            arrPaymentMethods[i] = toEbayBuyerPaymentMethod(liberecoPaymentInformations.get(i).getPaymentMethod());
        }
        item.setPaymentMethods(arrPaymentMethods);

        if (!CollectionUtils.isEmpty(liberecoListing.getShippingInformations()) && (liberecoListing.getShippingInformations().size() == 1)) {
            List<LiberecoShippingInformation> shippingInformations = liberecoListing.getShippingInformations();
            LiberecoShippingInformation shippingInformation = shippingInformations.get(0);
            ShippingDetailsType shippingDetails = toEbayShippingDetails(shippingInformation);
            item.setShippingDetails(shippingDetails);
        }
        item.setPayPalEmailAddress(ebayListing.getPaypalEmail());
        item.setReturnPolicy(toReturnPolicy(ebayListing.getReturnPolicy()));
        return item;
    }

    private BuyerPaymentMethodCodeType toEbayBuyerPaymentMethod(PaymentMethod paymentMethod) {
        switch (paymentMethod) {
        case AM_EX:
            return BuyerPaymentMethodCodeType.AM_EX;
        case CASH_IN_PERSON:
            return BuyerPaymentMethodCodeType.CASH_IN_PERSON;
        case CASH_ON_PICKUP:
            return BuyerPaymentMethodCodeType.CASH_ON_PICKUP;
        case CC_ACCEPTED:
            return BuyerPaymentMethodCodeType.CC_ACCEPTED;
        case COD:
            return BuyerPaymentMethodCodeType.COD;
        case PAYPAL:
            return BuyerPaymentMethodCodeType.PAY_PAL;
        }
        throw new IllegalArgumentException("No valid BuyerPaymentMethodCodeType found for " + paymentMethod);
    }

    public ShippingDetailsType toEbayShippingDetails(LiberecoShippingInformation shippingInformation) {
        ShippingDetailsType shippingDetails = new ShippingDetailsType();
        shippingDetails.setShippingType(toEbayShippingType(shippingInformation.getShippingType()));

        ShippingServiceOptionsType shippingServiceOption = new ShippingServiceOptionsType();
        shippingServiceOption.setShippingService(shippingInformation.getShippingService());
        AmountType at = new AmountType();
        at.setValue(shippingInformation.getShippingCost());
        shippingServiceOption.setShippingServiceCost(at);

        ShippingServiceOptionsType[] shippingServiceOptions = { shippingServiceOption };
        shippingDetails.setShippingServiceOptions(shippingServiceOptions);
        return shippingDetails;
    }

    private ShippingTypeCodeType toEbayShippingType(ShippingType shippingType) {
        switch (shippingType) {
        case FLAT:
            return ShippingTypeCodeType.FLAT;
        }
        throw new IllegalArgumentException("No Ebay ShippingTypeCodeType found for shippingType : " + shippingType);

    }

    private Integer toConditionId(ListingCondition listingCondition) {
        switch (listingCondition) {
        case MANUFACTURER_REFURBISHED:
            return 2000;
        case NEW:
            return 1000;
        case PARTS_NOT_WORKING:
            return 7000;
        case SELLER_REFURBISHED:
            return 2500;
        case USED:
            return 3000;
        default:
            throw new IllegalArgumentException("No valid value found for EBay Listing : " + listingCondition);
        }
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
