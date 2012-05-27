/**
 *  Copyright (C) 2011 Dushkin Digital Media, LLC
 *  500 E 77th Street, Ste. 806
 *  New York, NY 10162
 *
 *  All rights reserved.
 **/
package com.libereco.core.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author Aleksandar
 * 
 */
@Entity
@Table(name = "pendingMarketplaceAuthorizations")
@SuppressWarnings("serial")
public class PendingMarketplaceAuthorizations implements Serializable{

    // TODO: Replace individual token-related members with AuthRequestToken data
    // member
    private String requestToken;
    private String requestTokenSecret;
    
    @EmbeddedId
    protected UserMarketplaceKey key;
    
    public PendingMarketplaceAuthorizations() {
        super();
    }

    public PendingMarketplaceAuthorizations(LiberecoUser user, Marketplace marketplace, String requestToken) {
        this(user, marketplace, requestToken, null);
    }

    public PendingMarketplaceAuthorizations(LiberecoUser user, Marketplace marketplace, String requestToken, String requestTokenSecret) {
        this.requestToken = requestToken;
        this.requestTokenSecret = requestTokenSecret;
        this.key = new UserMarketplaceKey(user.getId(), marketplace.getId());
    }
    
    public void setKey(UserMarketplaceKey key) {
        this.key = key;
    }
    
    public UserMarketplaceKey getKey() {
        return key;
    }

    @Column(name = "requestToken", length = 128)
    public String getRequestToken() {
        return requestToken;
    }

    public void setRequestToken(String requestToken) {
        this.requestToken = requestToken;
    }

    @Column(name = "requestTokenSecret", length = 128)
    public String getRequestTokenSecret() {
        return requestTokenSecret;
    }

    public void setRequestTokenSecret(String requestTokenSecret) {
        this.requestTokenSecret = requestTokenSecret;
    }

}
