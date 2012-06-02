package com.libereco.web.external.ebay;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.ebay.sdk.ApiContext;
import com.ebay.sdk.call.AddItemCall;
import com.ebay.sdk.util.eBayUtil;
import com.ebay.soap.eBLBaseComponents.AmountType;
import com.ebay.soap.eBLBaseComponents.BestOfferDetailsType;
import com.ebay.soap.eBLBaseComponents.BuyerPaymentMethodCodeType;
import com.ebay.soap.eBLBaseComponents.CategoryType;
import com.ebay.soap.eBLBaseComponents.CountryCodeType;
import com.ebay.soap.eBLBaseComponents.CurrencyCodeType;
import com.ebay.soap.eBLBaseComponents.DetailLevelCodeType;
import com.ebay.soap.eBLBaseComponents.FeeType;
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
    private AddItemCall addItemCall;

    @Autowired
    public EbayAddListingClient(ApiContext apiContext, Environment environment) {
        this.apiContext = apiContext;
        this.apiContext.getApiCredential().seteBayToken("AgAAAA**AQAAAA**aAAAAA**u5LJTw**nY+sHZ2PrBmdj6wVnY+sEZ2PrA2dj6wFk4GhCJiKpQ6dj6x9nY+seQ**5dEBAA**AAMAAA**K3YLHsnW2N67j6uI2leG4Ear6Z67/0ktEDFc439VTfKE/nB8uzBEBD9H7pwexITBtFgSp5InGJyYnCIx1S+KblwvVD+qrvU5pEFviGlKhf1EwaGy3ha2X1GB+bmBELGbN9h3PdeS5JRZqVgAsRM8KM4mjTy0ojYJ6F0uSz7v8lPAmo5PXkKB72PNWBqPJDNfklf8zc3isSsOFvqYDlFY7iZ36W01wR2AVf0AXmWyM3aphtAVfMSGHzXayPqw3djEq96upt8P6mRrcu8Gj1JKTyHwg1gT6wJs6X/Hu4R4506wvlDZ1ijB0Hi7NxBDh4VdmhRDe6G2GFJjkisHcwkbpKR0dFhZxTOwL3JtG+CQ5IB/tc8g0Ns37FAYj1K5Zjtlwya9wVYhiTPJTa9LmzhsSO0zvwr6zpVm/H5ULGSdVbxx0R2bNV2nGj2zOxIMotvwcwvXyXMfXxG1ouIIXnSIMHIFopE8GXsghj12njvg+evLXXYCB95GlMQHBxsjDkJsHljixaZZbQPDSKt77NxonpUACTNZN/CqDDzF1Dig+Iy61r3SD+3p//wXCF8u2yXMZ64XIYhMsxL2EHUyPmfJh/dvwxOZOSaIg3srm4SewhhQmK6wMSYhZX3g12xQf1JajBJot3eTKbbnPTwO67LlcaLzwl6mhLyIPpE7d2hiKOjixJKX2U2Y5HWyEfG9UU0i+ABiwQAFyqbqQI9PHHofkDBUyE/bvBH0CBOixeyqFmyflKEgANPLFLCOFt6f+PnT");
        this.environment = environment;
        this.addItemCall = new AddItemCall(apiContext);
        initAddItemCall();
    }

    private void initAddItemCall() {
        // Let the call object to automatically generate UUID for my item.
        addItemCall.setAutoSetItemUUID(false);
        addItemCall.setSite(SiteCodeType.US);

        // Set detail level to retrieve item description.
        addItemCall.addDetailLevel(DetailLevelCodeType.ITEM_RETURN_DESCRIPTION);
    }

    public void addListing(EbayListing ebayListing) {

        try {
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
            FeeType ft = eBayUtil.findFeeByName(fees.getFee(), "ListingFee");
            System.out.println("Listing fee: " + ft.getFee().getValue());
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
        // TODO : A listing's duration is the time (expressed in days) that the
        // listing will be active on the eBay site.
        item.setListingDuration("Days_7");

        // TODO : Domain Model does not have region,location, currency
        // information
        item.setRegionID("0");
        item.setLocation("San Jose, CA");
        item.setCurrency(CurrencyCodeType.USD);
        item.setCountry(CountryCodeType.US);

        item.setQuantity(liberecoListing.getQuantity());

        AmountType startPrice = toAmountType(ebayListing.getStartPrice());
        item.setStartPrice(startPrice);
//        item.setReservePrice(toAmountType(ebayListing.getReservePrice()));
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
        CategoryType cat = new CategoryType();
        cat.setCategoryID("139971");
        item.setPrimaryCategory(cat);
        item.setDispatchTimeMax(ebayListing.getDispatchTimeMax());
        item.setReturnPolicy(toReturnPolicy(ebayListing.getReturnPolicy()));
        
        item.setCurrency(CurrencyCodeType.USD);

        // TODO: Test
        // api.setPictureFiles(this.getPicturePathList());

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
