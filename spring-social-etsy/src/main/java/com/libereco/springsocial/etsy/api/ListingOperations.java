package com.libereco.springsocial.etsy.api;

public interface ListingOperations {

    Listing createListing(Listing listing);

    String getListing(int listingId);
   
    void updateListing(Listing listing);
    
    void deleteListing(int listingId);
    
    String uploadListingImage(int listingId, String imagePath);

    String getImageForListing(int listingId, int listingImageId);

    String findAllListingForImages(int listingId);
    
}
