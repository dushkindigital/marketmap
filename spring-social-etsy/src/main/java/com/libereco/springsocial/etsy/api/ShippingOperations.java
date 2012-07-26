package com.libereco.springsocial.etsy.api;


public interface ShippingOperations {

    String createShippingTemplate(ShippingTemplateInfo shippingTemplate);

    public String getCountries();
    
    String getShippingTemplateInfo();
    
    String getShippingTemplateForUser();
    
    String createPaymentTemplate(PaymentTemplateInfo paymentTemplateInfo);
    
    String findAllUserPaymentTemplates(String userId);
    
    String getCategory(String tag);
    
}
