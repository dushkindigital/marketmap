package com.libereco.springsocial.etsy.api;

import org.springframework.social.ApiBinding;

public interface EtsyApi extends ApiBinding {

    UserOperations userOperations();

    ListingOperations listingOperations();

    PaymentOperations paymentOperations();
    
    ShippingOperations shippingOperations();
    
    CategoryOperations categoryOperations();
    
    CountryOperations countryOperations();
}
