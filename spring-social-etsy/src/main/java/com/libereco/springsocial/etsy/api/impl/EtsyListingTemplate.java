package com.libereco.springsocial.etsy.api.impl;

import org.springframework.core.io.Resource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.libereco.springsocial.etsy.api.EtsyListing;
import com.libereco.springsocial.etsy.api.EtsyListingOperations;

class EtsyListingTemplate extends AbstractEtsyOperations implements EtsyListingOperations {

    private final RestTemplate restTemplate;

    public EtsyListingTemplate(RestTemplate restTemplate, boolean isAuthorizedForUser) {
        super(isAuthorizedForUser);
        this.restTemplate = restTemplate;
    }

    @Override
    public EtsyListing createListing(EtsyListing listing) {
        return restTemplate.postForEntity(buildUri("listings"), listing, EtsyListing.class).getBody();
    }

    @Override
    public String uploadListingImage(int listingId, Resource resource) {
        MultiValueMap<String, Object> parts = new
                LinkedMultiValueMap<String, Object>();
        parts.add("listing_id", String.valueOf(listingId));
        parts.add("image", resource);
        return restTemplate.postForObject(buildUri("listings/" + listingId + "/images"), parts, String.class);
    }

    @Override
    public String getImageForListing(int listingId, int listingImageId) {
        return restTemplate.getForObject(buildUri("listings/" + listingId + "/images/" + listingImageId), String.class);
    }

    @Override
    public String findAllImagesForListing(int listingId) {
        return restTemplate.getForObject(buildUri("listings/" + listingId + "/images"), String.class);
    }

    @Override
    public String getListing(int listingId) {
        return restTemplate.getForObject(buildUri("listings/" + listingId), String.class);
    }

    @Override
    public void updateListing(EtsyListing listing) {
        restTemplate.put(buildUri("listings/" + listing.getListingId()), listing);
    }

    @Override
    public void deleteListing(int listingId) {
        restTemplate.delete(buildUri("listings/" + listingId));
    }

}
