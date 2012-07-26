package com.libereco.springsocial.etsy.api;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(using = EtsyUserArrayDeserializer.class)
public class EtsyUserCollection {

    private List<EtsyUser> etsyUsers;

    public List<EtsyUser> getEtsyUsers() {
        return etsyUsers;
    }

    public void setEtsyUsers(List<EtsyUser> etsyUsers) {
        this.etsyUsers = etsyUsers;
    }
    
    
}
