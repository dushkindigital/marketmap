package com.libereco.web.external.ebay;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.ebay.sdk.ApiContext;
import com.ebay.sdk.call.AddItemCall;
import com.ebay.soap.eBLBaseComponents.AmountType;
import com.ebay.soap.eBLBaseComponents.BestOfferDetailsType;
import com.ebay.soap.eBLBaseComponents.BuyerPaymentMethodCodeType;
import com.ebay.soap.eBLBaseComponents.CategoryType;
import com.ebay.soap.eBLBaseComponents.CountryCodeType;
import com.ebay.soap.eBLBaseComponents.CurrencyCodeType;
import com.ebay.soap.eBLBaseComponents.DetailLevelCodeType;
import com.ebay.soap.eBLBaseComponents.FeesType;
import com.ebay.soap.eBLBaseComponents.ItemType;
import com.ebay.soap.eBLBaseComponents.ListingEnhancementsCodeType;
import com.ebay.soap.eBLBaseComponents.ListingTypeCodeType;
import com.ebay.soap.eBLBaseComponents.ReturnPolicyType;
import com.ebay.soap.eBLBaseComponents.ShippingDetailsType;
import com.ebay.soap.eBLBaseComponents.ShippingServiceOptionsType;
import com.ebay.soap.eBLBaseComponents.ShippingTypeCodeType;
import com.ebay.soap.eBLBaseComponents.SiteCodeType;
import com.ebay.soap.eBLBaseComponents.VATDetailsType;
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
            AddItemCall addItemCall = new AddItemCall(apiContext);
            addItemCall.setAutoSetItemUUID(false);
            addItemCall.setSite(SiteCodeType.US);
            // Set detail level to retrieve item description.
            addItemCall.addDetailLevel(DetailLevelCodeType.ITEM_RETURN_DESCRIPTION);

            ItemType item = toItemType(ebayListing);
            addItemCall.setItem(item);
            /**
             * Defines a single new item and lists it on a specified eBay site.
             * Also for Half.com. Returns the item ID for the new listing, and
             * returns fees the seller will incur for the listing (not including
             * the Final Value Fee, which cannot be calculated until the item is
             * sold).
             */
            FeesType fees = addItemCall.addItem();
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
        item.setListingDuration(ebayListing.getListingDuration().getName());

        // TODO : Domain Model does not have region,location, currency
        // information
        item.setRegionID("0");
        item.setLocation("San Jose, CA");
        item.setCurrency(CurrencyCodeType.USD);
        item.setCountry(CountryCodeType.US);

        item.setQuantity(liberecoListing.getQuantity());

        AmountType startPrice = toAmountType(ebayListing.getStartPrice());
        item.setStartPrice(startPrice);
        // item.setReservePrice(toAmountType(ebayListing.getReservePrice()));
        item.setBuyItNowPrice(toAmountType(ebayListing.getBuyItNowPrice()));
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

        item.setVATDetails(toVatDetails(ebayListing.getVatPercent()));

        item.setAutoPay(ebayListing.getAutoPay());

        List<ListingEnhancementsCodeType> listEnhancementsCodeTypes = new ArrayList<ListingEnhancementsCodeType>();
        if (ebayListing.getBorderChecked() != null && ebayListing.getBorderChecked()) {
            listEnhancementsCodeTypes.add(ListingEnhancementsCodeType.BORDER);
        }
        if (ebayListing.getBoldTitleChecked() != null && ebayListing.getBoldTitleChecked()) {
            listEnhancementsCodeTypes.add(ListingEnhancementsCodeType.BOLD_TITLE);
        }

        ListingEnhancementsCodeType[] enhancements = listEnhancementsCodeTypes.toArray(new ListingEnhancementsCodeType[0]);
        item.setListingEnhancement(enhancements);

        if (ebayListing.getBestOfferEnabled() != null && ebayListing.getBestOfferEnabled()) {
            BestOfferDetailsType bo = new BestOfferDetailsType();
            bo.setBestOfferEnabled(ebayListing.getBestOfferEnabled());
            item.setBestOfferDetails(bo);
        }

        // TODO : How to get category information

        CategoryType cat = new CategoryType();
        cat.setCategoryID("139971");
        item.setPrimaryCategory(cat);
        item.setDispatchTimeMax(ebayListing.getDispatchTimeMax());
        item.setReturnPolicy(toReturnPolicy(ebayListing.getReturnPolicy()));

        item.setCurrency(CurrencyCodeType.USD);

        item.setListingType(ListingTypeCodeType.FIXED_PRICE_ITEM);

        // 1000 - New
        item.setConditionID(1000);
        return item;
    }

    private ReturnPolicyType toReturnPolicy(ReturnPolicy returnPolicy) {
        ReturnPolicyType returnPolicyType = new ReturnPolicyType();
        returnPolicyType.setReturnsAcceptedOption("ReturnsAccepted");
        return returnPolicyType;
    }

    private VATDetailsType toVatDetails(Float vatPercent) {
        VATDetailsType vatDetails = new VATDetailsType();
        vatDetails.setVATPercent(vatPercent);
        return vatDetails;
    }

    private AmountType toAmountType(double price) {
        AmountType amount = new AmountType();
        amount.setCurrencyID(CurrencyCodeType.USD);
        amount.setValue(price);
        return amount;
    }
}
