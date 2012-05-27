package com.libereco.core.service;

import com.libereco.core.domain.MarketplaceAuthorizations;
import com.libereco.core.domain.MarketplaceAuthorizationsCompositeKey;
import java.util.List;
import org.springframework.roo.addon.layers.service.RooService;

@RooService(domainTypes = { com.libereco.core.domain.MarketplaceAuthorizations.class })
public interface MarketplaceAuthorizationsService {

	public abstract long countAllMarketplaceAuthorizationses();


	public abstract void deleteMarketplaceAuthorizations(MarketplaceAuthorizations marketplaceAuthorizations);


	public abstract MarketplaceAuthorizations findMarketplaceAuthorizations(MarketplaceAuthorizationsCompositeKey id);


	public abstract List<MarketplaceAuthorizations> findAllMarketplaceAuthorizationses();


	public abstract List<MarketplaceAuthorizations> findMarketplaceAuthorizationsEntries(int firstResult, int maxResults);


	public abstract void saveMarketplaceAuthorizations(MarketplaceAuthorizations marketplaceAuthorizations);


	public abstract MarketplaceAuthorizations updateMarketplaceAuthorizations(MarketplaceAuthorizations marketplaceAuthorizations);

}
