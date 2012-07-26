package com.libereco.springsocial.etsy.api.impl;

import org.springframework.web.client.RestTemplate;

import com.libereco.springsocial.etsy.api.PaymentTemplateInfo;
import com.libereco.springsocial.etsy.api.ShippingOperations;
import com.libereco.springsocial.etsy.api.ShippingTemplateInfo;

public class ShippingTemplate extends AbstractEtsyOperations implements ShippingOperations {

    private final RestTemplate restTemplate;

    public ShippingTemplate(RestTemplate restTemplate, boolean isAuthorizedForUser) {
        super(isAuthorizedForUser);
        this.restTemplate = restTemplate;
    }

    @Override
    public String createShippingTemplate(ShippingTemplateInfo shippingTemplateInfo) {
        return restTemplate.postForEntity(buildUri("shipping/templates"), shippingTemplateInfo, String.class).getBody();
    }

    @Override
    public String createPaymentTemplate(PaymentTemplateInfo paymentTemplateInfo) {
        return restTemplate.postForEntity(buildUri("payments/templates"), paymentTemplateInfo, String.class).getBody();
    }

    @Override
    public String getShippingTemplateInfo() {
        return restTemplate.getForObject(buildUri("shipping/templates/1"), String.class);
    }

    @Override
    public String getCountries() {
        return restTemplate.getForObject(buildUri("countries"), String.class);
    }

    @Override
    public String getShippingTemplateForUser() {
        return restTemplate.getForObject(buildUri("users/14888629/shipping/templates"), String.class);
    }

    @Override
    public String findAllUserPaymentTemplates(String userId) {
        return restTemplate.getForObject(buildUri("users/" + userId + "/payments/templates"), String.class);
    }

    @Override
    public String getCategory(String tag) {
        return restTemplate.getForObject(buildUri("categories/" + tag), String.class);
    }
}
