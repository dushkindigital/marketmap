package com.libereco.springsocial.etsy.connect;

import org.springframework.social.connect.ApiAdapter;
import org.springframework.social.connect.ConnectionValues;
import org.springframework.social.connect.UserProfile;
import org.springframework.social.connect.UserProfileBuilder;

import com.libereco.springsocial.etsy.api.EtsyApi;
import com.libereco.springsocial.etsy.api.EtsyUser;

public class EtsyAdapter implements ApiAdapter<EtsyApi> {

    @Override
    public boolean test(EtsyApi etsy) {
        try {
            etsy.userOperations().getUserProfile();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void setConnectionValues(EtsyApi etsyApi, ConnectionValues values) {

    }

    @Override
    public UserProfile fetchUserProfile(EtsyApi etsy) {
        EtsyUser etsyUser = etsy.userOperations().getUserProfile();
        return new UserProfileBuilder().setEmail(etsyUser.getPrimaryEmail()).setUsername(etsyUser.getLoginName()).build();
    }

    @Override
    public void updateStatus(EtsyApi etsyApi, String message) {
        
    }

}
