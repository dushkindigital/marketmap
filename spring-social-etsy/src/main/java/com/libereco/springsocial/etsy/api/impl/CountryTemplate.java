package com.libereco.springsocial.etsy.api.impl;

import org.springframework.web.client.RestTemplate;

import com.libereco.springsocial.etsy.api.CountryOperations;

public class CountryTemplate extends AbstractEtsyOperations implements CountryOperations {

    private final RestTemplate restTemplate;

    public CountryTemplate(RestTemplate restTemplate, boolean isAuthorizedForUser) {
        super(isAuthorizedForUser);
        this.restTemplate = restTemplate;
    }
    
    @Override
    public String getCountries() {
        return restTemplate.getForObject(buildUri("countries"), String.class);
    }

}
