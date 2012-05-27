/**
 *  Copyright (C) 2011 Dushkin Digital Media, LLC
 *  500 E 77th Street, Ste. 806
 *  New York, NY 10162
 *
 *  All rights reserved.
 **/
package com.libereco.core.domain;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * @author Aleksandar
 * 
 */
@SuppressWarnings("serial")
@Entity
public class AbstractUserMarketplace implements Serializable {

    protected LiberecoUser user;

    protected Marketplace marketplace;

    @EmbeddedId
    protected UserMarketplaceKey key;

    public AbstractUserMarketplace() {
        super();
    }

    public AbstractUserMarketplace(LiberecoUser user, Marketplace marketplace) {
        super();
        this.user = user;
        this.marketplace = marketplace;
        key = new UserMarketplaceKey(user.getId(), marketplace.getId());
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    public LiberecoUser getUser() {
        return user;
    }

    public void setUser(LiberecoUser user) {
        this.user = user;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "marketplaceId", nullable = false)
    public Marketplace getMarketplace() {
        return marketplace;
    }

    public void setMarketplace(Marketplace marketplace) {
        this.marketplace = marketplace;
    }

    public UserMarketplaceKey getKey() {
        return key;
    }

    public void setKey(UserMarketplaceKey key) {
        this.key = key;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AbstractUserMarketplace other = (AbstractUserMarketplace) obj;
        if (key == null) {
            if (other.key != null)
                return false;
        } else if (!key.equals(other.key))
            return false;
        return true;
    }
}
