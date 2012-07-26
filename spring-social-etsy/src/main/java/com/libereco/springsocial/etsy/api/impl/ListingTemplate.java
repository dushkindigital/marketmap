package com.libereco.springsocial.etsy.api.impl;

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
    public String createListing(Listing listing) {
        return restTemplate.postForEntity(buildUri("listings"), listing, String.class).getBody();
    }

}
