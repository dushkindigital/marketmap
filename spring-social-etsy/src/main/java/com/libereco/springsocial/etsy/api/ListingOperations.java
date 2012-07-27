package com.libereco.springsocial.etsy.api;

public interface ListingOperations {

    String createListing(Listing listing);

    String uploadListingImage(int listingId, String imagePath);

    String getImageForListing(int listingId, int listingImageId);

    String findAllListingForImages(int listingId);
}
