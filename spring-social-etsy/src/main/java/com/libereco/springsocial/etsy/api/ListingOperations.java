package com.libereco.springsocial.etsy.api;

import org.springframework.core.io.Resource;

public interface ListingOperations {

    Listing createListing(Listing listing);

    String getListing(int listingId);
   
    void updateListing(Listing listing);
    
    void deleteListing(int listingId);
    
    String uploadListingImage(int listingId, Resource resource);

    String getImageForListing(int listingId, int listingImageId);

    String findAllListingForImages(int listingId);
    
}
