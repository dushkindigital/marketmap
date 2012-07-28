package com.libereco.springsocial.etsy.api.impl;

import org.springframework.web.client.RestTemplate;

import com.libereco.springsocial.etsy.api.CategoryOperations;

public class CategoryTemplate extends AbstractEtsyOperations implements CategoryOperations {

    private final RestTemplate restTemplate;

    public CategoryTemplate(RestTemplate restTemplate, boolean isAuthorizedForUser) {
        super(isAuthorizedForUser);
        this.restTemplate = restTemplate;
    }
    
    @Override
    public String getCategory(String tag) {
        return restTemplate.getForObject(buildUri("categories/" + tag), String.class);
    }

}
