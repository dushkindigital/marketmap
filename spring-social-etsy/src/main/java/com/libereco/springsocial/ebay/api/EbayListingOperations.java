package com.libereco.springsocial.ebay.api;

import com.ebay.soap.eBLBaseComponents.EndReasonCodeType;
import com.ebay.soap.eBLBaseComponents.ItemType;

public interface EbayListingOperations {

    ItemType createEbayListing(ItemType listing, String[] pictureUrls);

    ItemType getEbayListing(String listingId);

    ItemType updateEbayListing(ItemType listing, String[] pictureUrls);

    void deleteEbayListing(String listingId, EndReasonCodeType reason);

}
