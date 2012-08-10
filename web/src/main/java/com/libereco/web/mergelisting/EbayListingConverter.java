package com.libereco.web.mergelisting;

import java.util.List;

import org.springframework.util.CollectionUtils;

import com.ebay.soap.eBLBaseComponents.AmountType;
import com.ebay.soap.eBLBaseComponents.BuyerPaymentMethodCodeType;
import com.ebay.soap.eBLBaseComponents.CategoryType;
import com.ebay.soap.eBLBaseComponents.CountryCodeType;
import com.ebay.soap.eBLBaseComponents.CurrencyCodeType;
import com.ebay.soap.eBLBaseComponents.ItemType;
import com.ebay.soap.eBLBaseComponents.ListingTypeCodeType;
import com.ebay.soap.eBLBaseComponents.ReturnPolicyType;
import com.ebay.soap.eBLBaseComponents.ShippingDetailsType;
import com.ebay.soap.eBLBaseComponents.ShippingServiceOptionsType;
import com.ebay.soap.eBLBaseComponents.ShippingTypeCodeType;
import com.libereco.core.domain.LiberecoCategory;
import com.libereco.core.domain.LiberecoPaymentInformation;
import com.libereco.core.domain.LiberecoShippingInformation;
import com.libereco.core.domain.ListingCondition;
import com.libereco.core.domain.MergedEbayListing;
import com.libereco.core.domain.MergedLiberecoListing;
import com.libereco.core.domain.PaymentMethod;
import com.libereco.core.domain.ReturnPolicy;
import com.libereco.core.domain.ShippingType;
import com.libereco.core.exceptions.GenericLiberecoException;

public abstract class EbayListingConverter {

    public static ItemType toItemType(MergedLiberecoListing liberecoListing) {
        MergedEbayListing ebayListing = liberecoListing.getMergedEbayListing();
        ItemType item = new ItemType();
        item.setTitle(liberecoListing.getName());
        item.setDescription(liberecoListing.getDescription());

        CategoryType cat = toEbayCategoryType(liberecoListing.getCategory());
        item.setPrimaryCategory(cat);

        AmountType startPrice = toAmountType(ebayListing.getStartPrice());
        item.setStartPrice(startPrice);

        item.setCategoryMappingAllowed(true);

        item.setConditionID(toConditionId(liberecoListing.getListingCondition()));
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

    private static CategoryType toEbayCategoryType(LiberecoCategory category) {
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

    private static BuyerPaymentMethodCodeType toEbayBuyerPaymentMethod(PaymentMethod paymentMethod) {
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

    private static ShippingDetailsType toEbayShippingDetails(LiberecoShippingInformation shippingInformation) {
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

    private static ShippingTypeCodeType toEbayShippingType(ShippingType shippingType) {
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

    private static Integer toConditionId(ListingCondition listingCondition) {
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

    private static ReturnPolicyType toReturnPolicy(ReturnPolicy returnPolicy) {
        ReturnPolicyType returnPolicyType = new ReturnPolicyType();
        returnPolicyType.setReturnsAcceptedOption("ReturnsAccepted");
        return returnPolicyType;
    }

    private static AmountType toAmountType(double price) {
        AmountType amount = new AmountType();
        amount.setCurrencyID(CurrencyCodeType.USD);
        amount.setValue(price);
        return amount;
    }
}
