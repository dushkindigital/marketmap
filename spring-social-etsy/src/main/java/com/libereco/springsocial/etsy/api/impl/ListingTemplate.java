package com.libereco.springsocial.etsy.api.impl;

import org.springframework.core.io.FileSystemResource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.libereco.springsocial.etsy.api.Listing;
import com.libereco.springsocial.etsy.api.ListingOperations;

class ListingTemplate extends AbstractEtsyOperations implements ListingOperations {

    private final RestTemplate restTemplate;

    public ListingTemplate(RestTemplate restTemplate, boolean isAuthorizedForUser) {
        super(isAuthorizedForUser);
        this.restTemplate = restTemplate;
    }

    @Override
    public Listing createListing(Listing listing) {
        return restTemplate.postForEntity(buildUri("listings"), listing, Listing.class).getBody();
    }

    @Override
    public String uploadListingImage(int listingId, String imagePath) {
        MultiValueMap<String, Object> parts = new
                LinkedMultiValueMap<String, Object>();
        parts.add("listing_id", String.valueOf(listingId));
        parts.add("image", new
                FileSystemResource(imagePath));
        return restTemplate.postForObject(buildUri("listings/" + listingId + "/images"), parts, String.class);
    }

    @Override
    public String getImageForListing(int listingId, int listingImageId) {
        return restTemplate.getForObject(buildUri("listings/" + listingId + "/images/" + listingImageId), String.class);
    }

    @Override
    public String findAllListingForImages(int listingId) {
        return restTemplate.getForObject(buildUri("listings/" + listingId + "/images"), String.class);
    }

    @Override
    public String getListing(int listingId) {
        return restTemplate.getForObject(buildUri("listings/" + listingId), String.class);
    }

    @Override
    public void updateListing(Listing listing) {
        restTemplate.put(buildUri("listings/" + listing.getListingId()), listing);
    }

    @Override
    public void deleteListing(int listingId) {
        restTemplate.delete(buildUri("listings/" + listingId));
    }

}
