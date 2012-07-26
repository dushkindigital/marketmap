package com.libereco.springsocial.etsy.api;

import java.util.List;

public interface UserOperations {

    public List<EtsyUser> findAllUsers();

    public EtsyUser getUser(String userId);
    
    public EtsyUser getUserProfile();

}
