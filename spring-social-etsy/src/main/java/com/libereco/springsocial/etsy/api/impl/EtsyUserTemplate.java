package com.libereco.springsocial.etsy.api.impl;

import java.util.List;

import org.springframework.web.client.RestTemplate;

import com.libereco.springsocial.etsy.api.EtsyUser;
import com.libereco.springsocial.etsy.api.EtsyUserCollection;
import com.libereco.springsocial.etsy.api.EtsyUserOperations;

class EtsyUserTemplate extends AbstractEtsyOperations implements EtsyUserOperations {

    private final RestTemplate restTemplate;

    public EtsyUserTemplate(RestTemplate restTemplate, boolean isAuthorizedForUser) {
        super(isAuthorizedForUser);
        this.restTemplate = restTemplate;
    }

    @Override
    public List<EtsyUser> findAllUsers() {
        return restTemplate.getForObject(buildUri("users.json?limit=50&offset=0"), EtsyUserCollection.class).getEtsyUsers();
    }

    @Override
    public EtsyUser getUser(String userId) {
        return restTemplate.getForObject(buildUri("users/" + userId + ".json"), EtsyUser.class);
    }

    @Override
    public EtsyUser getUserProfile() {
        return restTemplate.getForObject(buildUri("users/shekhargulati.json"), EtsyUser.class);
    }

}
