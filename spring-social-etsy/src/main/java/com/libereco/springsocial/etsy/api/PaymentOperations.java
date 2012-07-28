package com.libereco.springsocial.etsy.api;

public interface PaymentOperations {

    String createPaymentTemplate(PaymentTemplateInfo paymentTemplateInfo);

    String findAllUserPaymentTemplates(String userId);
}
