package com.libereco.springsocial.ebay.api.impl;

import java.util.List;

import com.ebay.sdk.ApiContext;
import com.ebay.sdk.call.GetUserCall;
import com.ebay.soap.eBLBaseComponents.UserType;
import com.libereco.springsocial.ebay.api.EbayUser;
import com.libereco.springsocial.ebay.api.EbayUserOperations;

class EbayUserTemplate implements EbayUserOperations {

    private final ApiContext apiContext;

    public EbayUserTemplate(ApiContext apiContext) {
        this.apiContext = apiContext;
    }

    @Override
    public EbayUser getUserProfile() {
        return getUser("testuser_shekhargulati");
    }

    @Override
    public List<EbayUser> findAllUsers() {
        throw new UnsupportedOperationException("Ebay API does not support find all user Operation.");
    }

    @Override
    public EbayUser getUser(String userId) {
        GetUserCall getUserCall = new GetUserCall(apiContext);
        getUserCall.setUserID(userId);
        try {
            UserType user = getUserCall.getUser();
            return toEbayUser(user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private EbayUser toEbayUser(UserType user) {
        EbayUser ebayUser = new EbayUser();
        ebayUser.setLoginName(user.getUserID());
        ebayUser.setPrimaryEmail(user.getEmail());
        ebayUser.setUserId(user.getUserID());
        ebayUser.setCreationDate(user.getRegistrationDate().getTime());
        return ebayUser;
    }
}
