package com.libereco.springsocial.etsy.api.impl;

import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.social.oauth1.AbstractOAuth1ApiBinding;
import org.springframework.web.client.RestTemplate;

import com.libereco.springsocial.etsy.api.CategoryOperations;
import com.libereco.springsocial.etsy.api.CountryOperations;
import com.libereco.springsocial.etsy.api.EtsyApi;
import com.libereco.springsocial.etsy.api.ListingOperations;
import com.libereco.springsocial.etsy.api.PaymentOperations;
import com.libereco.springsocial.etsy.api.ShippingOperations;
import com.libereco.springsocial.etsy.api.UserOperations;

public class EtsyTemplate extends AbstractOAuth1ApiBinding implements EtsyApi {

    private UserOperations userOperations;
    private ListingOperations listingOperations;
    private PaymentOperations paymentOperations;
    private ShippingOperations shippingOperations;
    private CategoryOperations categoryOperations;
    private CountryOperations countryOperations;
    
    public EtsyTemplate(String consumerKey, String consumerSecret, String accessToken, String accessTokenSecret) {
        super(consumerKey, consumerSecret, accessToken, accessTokenSecret);
        registerEtsyModule(getRestTemplate());
        initSubApis();
    }

    @Override
    public UserOperations userOperations() {
        return userOperations;
    }

    @Override
    public ListingOperations listingOperations() {
        return listingOperations;
    }

    @Override
    public PaymentOperations paymentOperations() {
        return paymentOperations;
    }

    @Override
    public ShippingOperations shippingOperations() {
        return shippingOperations;
    }
    
    
    @Override
    public CategoryOperations categoryOperations() {
        return categoryOperations;
    }
    
    @Override
    public CountryOperations countryOperations() {
        return countryOperations;
    }
    
    private void registerEtsyModule(RestTemplate restTemplate) {
        List<HttpMessageConverter<?>> converters = restTemplate.getMessageConverters();
        for (HttpMessageConverter<?> converter : converters) {
            if (converter instanceof MappingJacksonHttpMessageConverter) {
                MappingJacksonHttpMessageConverter jsonConverter = (MappingJacksonHttpMessageConverter) converter;
                ObjectMapper objectMapper = new ObjectMapper();
                jsonConverter.setObjectMapper(objectMapper);
            }
        }
    }

    private void initSubApis() {
        this.userOperations = new UserTemplate(getRestTemplate(), isAuthorized());
        this.shippingOperations = new ShippingTemplate(getRestTemplate(), isAuthorized());
        this.listingOperations = new ListingTemplate(getRestTemplate(), isAuthorized());
        this.paymentOperations = new PaymentTemplate(getRestTemplate(), isAuthorized());
        this.categoryOperations = new CategoryTemplate(getRestTemplate(), isAuthorized());
        this.countryOperations = new CountryTemplate(getRestTemplate(), isAuthorized());
    }

}
