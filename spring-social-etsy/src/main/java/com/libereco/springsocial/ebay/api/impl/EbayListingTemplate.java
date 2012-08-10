package com.libereco.springsocial.ebay.api.impl;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import com.ebay.sdk.ApiContext;
import com.ebay.sdk.call.AddFixedPriceItemCall;
import com.ebay.sdk.call.EndFixedPriceItemCall;
import com.ebay.sdk.call.GetItemCall;
import com.ebay.sdk.call.ReviseFixedPriceItemCall;
import com.ebay.soap.eBLBaseComponents.DetailLevelCodeType;
import com.ebay.soap.eBLBaseComponents.EndReasonCodeType;
import com.ebay.soap.eBLBaseComponents.FeesType;
import com.ebay.soap.eBLBaseComponents.GalleryTypeCodeType;
import com.ebay.soap.eBLBaseComponents.ItemType;
import com.ebay.soap.eBLBaseComponents.PictureDetailsType;
import com.ebay.soap.eBLBaseComponents.SiteCodeType;
import com.libereco.springsocial.ebay.api.EbayListingOperations;

class EbayListingTemplate implements EbayListingOperations {

    private final Logger logger = Logger.getLogger(this.getClass());

    private ApiContext apiContext;

    @Inject
    public EbayListingTemplate(ApiContext apiContext) {
        this.apiContext = apiContext;
    }

    @Override
    public ItemType createListing(ItemType item, String[] pictureUrls) {
        try {
            AddFixedPriceItemCall addFixedPriceItemCall = new AddFixedPriceItemCall(apiContext);
            addFixedPriceItemCall.setAutoSetItemUUID(false);
            addFixedPriceItemCall.setSite(SiteCodeType.US);
            // Set detail level to retrieve item description.
            addFixedPriceItemCall.addDetailLevel(DetailLevelCodeType.ITEM_RETURN_DESCRIPTION);

            setPictureDetails(pictureUrls, item);
            addFixedPriceItemCall.setItem(item);

            FeesType fees = addFixedPriceItemCall.addFixedPriceItem();
            String itemId = item.getItemID();
            logger.debug("Created item with id : " + itemId);
            return item;
        } catch (Exception e) {
            throw new RuntimeException("Not able to create listing on Ebay at this moment.", e);
        }
    }

    @Override
    public ItemType getEbayListing(String listingId) {
        GetItemCall getItemCall = new GetItemCall(apiContext);
        try {
            return getItemCall.getItem(listingId);
        } catch (Exception e) {
            throw new RuntimeException("Not able to get listing data from Ebay at this moment.", e);
        }
    }

    @Override
    public ItemType updateEbayListing(ItemType item, String[] pictureUrls) {
        try {

            ReviseFixedPriceItemCall reviseFixedPriceItemCall = new ReviseFixedPriceItemCall(apiContext);
            reviseFixedPriceItemCall.setSite(SiteCodeType.US);
            // Set detail level to retrieve item description.
            reviseFixedPriceItemCall.addDetailLevel(DetailLevelCodeType.ITEM_RETURN_DESCRIPTION);
            setPictureDetails(pictureUrls, item);
            reviseFixedPriceItemCall.setItemToBeRevised(item);
            FeesType fees = reviseFixedPriceItemCall.reviseFixedPriceItem();
            String itemId = item.getItemID();
            System.out.println("Item: " + itemId);
            return item;
        } catch (Exception e) {
            throw new RuntimeException("Not able to revise listing on Ebay at this moment.", e);
        }
    }

    @Override
    public void deleteEbayListing(String itemId, EndReasonCodeType reason) {
        EndFixedPriceItemCall endFixedPriceItemCall = new EndFixedPriceItemCall();
        endFixedPriceItemCall.setApiContext(apiContext);
        endFixedPriceItemCall.setEndingReason(reason);
        endFixedPriceItemCall.setItemID(itemId);
        try {
            endFixedPriceItemCall.endFixedPriceItem();
        } catch (Exception e) {
            throw new RuntimeException("Not able to delist item from Ebay at this moment.", e);
        }
    }

    private void setPictureDetails(String[] pictureUrls, ItemType item) {
        if (pictureUrls != null && pictureUrls.length > 0) {
            PictureDetailsType pictureDetailsObj = new PictureDetailsType();
            pictureDetailsObj.setPictureURL(pictureUrls);
            // To specify a Gallery Image
            pictureDetailsObj.setGalleryType(GalleryTypeCodeType.GALLERY);
            item.setPictureDetails(pictureDetailsObj);
        }
    }

}
