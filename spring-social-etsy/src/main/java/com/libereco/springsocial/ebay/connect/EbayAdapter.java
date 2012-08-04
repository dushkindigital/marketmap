package com.libereco.springsocial.ebay.connect;

import org.springframework.social.connect.ApiAdapter;
import org.springframework.social.connect.ConnectionValues;
import org.springframework.social.connect.UserProfile;
import org.springframework.social.connect.UserProfileBuilder;

import com.libereco.springsocial.ebay.api.EbayApi;
import com.libereco.springsocial.ebay.api.EbayUser;

public class EbayAdapter implements ApiAdapter<EbayApi> {

    @Override
    public boolean test(EbayApi ebayApi) {
        try {
            ebayApi.userOperations().getUserProfile();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void setConnectionValues(EbayApi ebayApi, ConnectionValues values) {
        EbayUser ebayUser = ebayApi.userOperations().getUserProfile();
        values.setProviderUserId(ebayUser.getUserId());
        values.setDisplayName(ebayUser.getUserId());
    }

    @Override
    public UserProfile fetchUserProfile(EbayApi ebayAPi) {
        EbayUser ebayUser = ebayAPi.userOperations().getUserProfile();
        return new UserProfileBuilder().setEmail(ebayUser.getPrimaryEmail()).setUsername(ebayUser.getLoginName()).build();
    }

    @Override
    public void updateStatus(EbayApi ebayApi, String message) { 

    }

}
