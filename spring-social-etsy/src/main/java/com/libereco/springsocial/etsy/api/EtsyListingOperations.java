package com.libereco.springsocial.etsy.api;

import org.springframework.core.io.Resource;

public interface EtsyListingOperations {

    EtsyListing createListing(EtsyListing listing);

    String getListing(int listingId);

    EtsyListing updateListing(EtsyListing listing);

    void deleteListing(int listingId);

    String uploadListingImage(int listingId, Resource resource);

    String getImageForListing(int listingId, int listingImageId);

    String findAllImagesForListing(int listingId);

}
