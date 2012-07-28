package com.libereco.springsocial.etsy.api;

public interface ShippingOperations {

    String createShippingTemplate(ShippingTemplateInfo shippingTemplate);

    String getShippingTemplateInfo();
    
    String getShippingTemplateForUser();
    
}
