package com.libereco.springsocial.etsy.api.impl;

import org.springframework.web.client.RestTemplate;

import com.libereco.springsocial.etsy.api.PaymentOperations;
import com.libereco.springsocial.etsy.api.PaymentTemplateInfo;

public class PaymentTemplate extends AbstractEtsyOperations implements PaymentOperations {

    private final RestTemplate restTemplate;

    public PaymentTemplate(RestTemplate restTemplate, boolean isAuthorizedForUser) {
        super(isAuthorizedForUser);
        this.restTemplate = restTemplate;
    }
    
    @Override
    public String createPaymentTemplate(PaymentTemplateInfo paymentTemplateInfo) {
        return restTemplate.postForEntity(buildUri("payments/templates"), paymentTemplateInfo, String.class).getBody();
    }

    @Override
    public String findAllUserPaymentTemplates(String userId) {
        return restTemplate.getForObject(buildUri("users/" + userId + "/payments/templates"), String.class);
    }


}
