package com.libereco.springsocial.ebay.api;

import java.util.List;

public interface EbayUserOperations {

    public List<EbayUser> findAllUsers();

    public EbayUser getUser(String userId);
    
    public EbayUser getUserProfile();

}
