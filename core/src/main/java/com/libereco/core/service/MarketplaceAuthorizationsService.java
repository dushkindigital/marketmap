package com.libereco.core.service;

import java.util.List;

import com.libereco.core.domain.MarketplaceAuthorizations;
import com.libereco.core.domain.MarketplaceAuthorizationsCompositeKey;

public interface MarketplaceAuthorizationsService {

    public abstract long countAllMarketplaceAuthorizationses();

    public abstract void deleteMarketplaceAuthorizations(MarketplaceAuthorizations marketplaceAuthorizations);

    public abstract MarketplaceAuthorizations findMarketplaceAuthorizations(MarketplaceAuthorizationsCompositeKey id);

    public abstract List<MarketplaceAuthorizations> findAllMarketplaceAuthorizationses();

    public abstract List<MarketplaceAuthorizations> findMarketplaceAuthorizationsEntries(int firstResult, int maxResults);

    public abstract void saveMarketplaceAuthorizations(MarketplaceAuthorizations marketplaceAuthorizations);

    public abstract MarketplaceAuthorizations updateMarketplaceAuthorizations(MarketplaceAuthorizations marketplaceAuthorizations);

    public abstract List<MarketplaceAuthorizations> findAllMarketplaceAuthorizationsForUser(Long id);

}
