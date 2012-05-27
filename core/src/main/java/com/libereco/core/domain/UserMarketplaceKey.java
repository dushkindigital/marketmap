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
import javax.persistence.Embeddable;

@SuppressWarnings("serial")
@Embeddable
public class UserMarketplaceKey implements Serializable {

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((marketplaceId == null) ? 0 : marketplaceId.hashCode());
        result = prime * result + ((userId == null) ? 0 : userId.hashCode());
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
        UserMarketplaceKey other = (UserMarketplaceKey) obj;
        if (marketplaceId == null) {
            if (other.marketplaceId != null)
                return false;
        } else if (!marketplaceId.equals(other.marketplaceId))
            return false;
        if (userId == null) {
            if (other.userId != null)
                return false;
        } else if (!userId.equals(other.userId))
            return false;
        return true;
    }

    @Column
    private Long userId;

    @Column
    private Long marketplaceId;

    public UserMarketplaceKey(Long userId, Long marketplaceId) {
        super();
        this.userId = userId;
        this.marketplaceId = marketplaceId;
    }
    
    public UserMarketplaceKey(){
        
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getMarketplaceId() {
        return marketplaceId;
    }

    public void setMarketplaceId(Long marketplaceId) {
        this.marketplaceId = marketplaceId;
    }

}