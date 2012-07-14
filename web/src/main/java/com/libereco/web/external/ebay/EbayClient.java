package com.libereco.web.external.ebay;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.ebay.sdk.ApiContext;
import com.ebay.sdk.call.AddFixedPriceItemCall;
import com.ebay.sdk.call.EndFixedPriceItemCall;
import com.ebay.sdk.call.GetCategoriesCall;
import com.ebay.sdk.call.GeteBayDetailsCall;
import com.ebay.sdk.call.ReviseFixedPriceItemCall;
import com.ebay.soap.eBLBaseComponents.AmountType;
import com.ebay.soap.eBLBaseComponents.BuyerPaymentMethodCodeType;
import com.ebay.soap.eBLBaseComponents.CategoryType;
import com.ebay.soap.eBLBaseComponents.CountryCodeType;
import com.ebay.soap.eBLBaseComponents.CurrencyCodeType;
import com.ebay.soap.eBLBaseComponents.DetailLevelCodeType;
import com.ebay.soap.eBLBaseComponents.DetailNameCodeType;
import com.ebay.soap.eBLBaseComponents.EndReasonCodeType;
import com.ebay.soap.eBLBaseComponents.FeesType;
import com.ebay.soap.eBLBaseComponents.GalleryTypeCodeType;
import com.ebay.soap.eBLBaseComponents.ItemType;
import com.ebay.soap.eBLBaseComponents.ListingTypeCodeType;
import com.ebay.soap.eBLBaseComponents.PictureDetailsType;
import com.ebay.soap.eBLBaseComponents.ReturnPolicyType;
import com.ebay.soap.eBLBaseComponents.ShippingDetailsType;
import com.ebay.soap.eBLBaseComponents.ShippingServiceDetailsType;
import com.ebay.soap.eBLBaseComponents.ShippingServiceOptionsType;
import com.ebay.soap.eBLBaseComponents.ShippingTypeCodeType;
import com.ebay.soap.eBLBaseComponents.SiteCodeType;
import com.libereco.core.domain.DelistingReason;
import com.libereco.core.domain.EbayListing;
import com.libereco.core.domain.LiberecoCategory;
import com.libereco.core.domain.LiberecoListing;
import com.libereco.core.domain.LiberecoPaymentInformation;
import com.libereco.core.domain.LiberecoShippingInformation;
import com.libereco.core.domain.ListingCondition;
import com.libereco.core.domain.PaymentMethod;
import com.libereco.core.domain.ReturnPolicy;
import com.libereco.core.domain.ShippingType;
import com.libereco.core.exceptions.ExternalServiceException;
import com.libereco.core.exceptions.GenericLiberecoException;

@Component
public class EbayClient {

    private ApiContext apiContext;
    private Environment environment;

    private final Logger logger = Logger.getLogger(this.getClass());

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
            ebayListing.setEbayItemId(itemId);

            String ebayItemUrl = environment.getProperty("libereco.ebay.item.url");
            ebayItemUrl += itemId;
            ebayListing.setEbayItemUrl(ebayItemUrl);
            return ebayListing;
        } catch (Exception e) {
            String exceptionMessage = getExceptionMessage(e);
            throw new ExternalServiceException("Not able to create listing on Ebay at this moment.", e);
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
            throw new ExternalServiceException("Not able to revise listing on Ebay at this moment.", e);
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
            throw new ExternalServiceException("Not able to delist item from Ebay at this moment.", e);
        }
    }

    public ShippingServiceDetailsType[] getEbayShippingServiceDetails(DetailNameCodeType detailName, String token) {
        this.apiContext
                .getApiCredential()
                .seteBayToken(token);
        GeteBayDetailsCall geteBayDetailsCall = new GeteBayDetailsCall(apiContext);
        geteBayDetailsCall.setDetailName(new DetailNameCodeType[] { detailName });
        try {
            geteBayDetailsCall.geteBayDetails();
            ShippingServiceDetailsType[] shippingServiceDetails = geteBayDetailsCall.getReturnedShippingServiceDetails();
            return shippingServiceDetails;
        } catch (Exception e) {
            throw new ExternalServiceException("Not able to get ebay details from Ebay at this moment.", e);
        }
    }

    public void getEbayCategories(String token) {
        this.apiContext
                .getApiCredential()
                .seteBayToken(token);
        GetCategoriesCall getCategoriesCall = new GetCategoriesCall(apiContext);
        getCategoriesCall.setDetailLevel(new DetailLevelCodeType[] { DetailLevelCodeType.RETURN_ALL });
        try {
            CategoryType[] categories = getCategoriesCall.getCategories();
            for (CategoryType categoryType : categories) {
                logger.info(categoryType.getCategoryName() + " , " + categoryType.getCategoryID());
            }
        } catch (Exception e) {
            throw new ExternalServiceException("Not able to get categories from Ebay at this moment.", e);
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

        throw new GenericLiberecoException("No Ebay EndReasonCodeType found for delisting reason : " + reason);
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

        CategoryType cat = toEbayCategoryType(ebayListing.getLiberecoListing().getCategory());
        item.setPrimaryCategory(cat);

        AmountType startPrice = toAmountType(ebayListing.getStartPrice());
        item.setStartPrice(startPrice);

        item.setCategoryMappingAllowed(true);

        item.setConditionID(toConditionId(ebayListing.getLiberecoListing().getListingCondition()));
        item.setDispatchTimeMax(ebayListing.getDispatchTimeMax());
        item.setListingDuration(ebayListing.getListingDuration().getName());
        item.setListingType(ListingTypeCodeType.FIXED_PRICE_ITEM);

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

    private CategoryType toEbayCategoryType(LiberecoCategory category) {
        CategoryType cat = new CategoryType();
        switch (category) {
        case CAT_BOOKS:
            cat.setCategoryID("116110");
            break;
        case CAT_CLOTHING_SHOES_JEWELLERY:
            cat.setCategoryID("158776");
            break;
        case CAT_COMPUTER_OFFICE:
            cat.setCategoryID("102490");
            break;
        case CAT_ELECTRONICS:
            cat.setCategoryID("102491");
            break;
        case CAT_GROCERY_HEALTH_BEAUTY:
            cat.setCategoryID("4256");
            break;
        case CAT_HOME_GARDEN_PETS:
            cat.setCategoryID("146110");
        case CAT_MOVIES_MUSIC_GAME:
            cat.setCategoryID("175691");
            break;
        case CAT_SPORTS_OUTDOOR:
            cat.setCategoryID("173633");
            break;
        case CAT_TOYS_KIDS_BABY:
            cat.setCategoryID("50306");
            break;
        }
        return cat;
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
        throw new GenericLiberecoException("No valid BuyerPaymentMethodCodeType found for " + paymentMethod);
    }

    private ShippingDetailsType toEbayShippingDetails(LiberecoShippingInformation shippingInformation) {
        ShippingDetailsType shippingDetails = new ShippingDetailsType();
        shippingDetails.setShippingType(toEbayShippingType(shippingInformation.getShippingType()));

        ShippingServiceOptionsType shippingServiceOption = new ShippingServiceOptionsType();
        shippingServiceOption.setShippingService(shippingInformation.getShippingService().name());
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
        case CALCULATED:
            return ShippingTypeCodeType.CALCULATED;
        case CALCULATED_DOMESTIC_FLAT_INTERNATIONAL:
            return ShippingTypeCodeType.CALCULATED_DOMESTIC_FLAT_INTERNATIONAL;
        case CUSTOM_CODE:
            return ShippingTypeCodeType.CUSTOM_CODE;
        case FLAT_DOMESTIC_CALCULATED_INTERNATIONAL:
            return ShippingTypeCodeType.FLAT_DOMESTIC_CALCULATED_INTERNATIONAL;
        case FREE:
            return ShippingTypeCodeType.FREE;
        case FREIGHT:
            return ShippingTypeCodeType.FREIGHT;
        case FREIGHT_FLAT:
            return ShippingTypeCodeType.FREIGHT_FLAT;
        case NOT_SPECIFIED:
            return ShippingTypeCodeType.NOT_SPECIFIED;
        }
        throw new GenericLiberecoException("No Ebay ShippingTypeCodeType found for shippingType : " + shippingType);

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
            throw new GenericLiberecoException("No valid value found for EBay Listing : " + listingCondition);
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
