package com.libereco.core.service;

import java.util.List;
import org.springframework.roo.addon.layers.service.RooService;

import com.libereco.core.domain.Marketplace;

@RooService(domainTypes = { com.libereco.core.domain.Marketplace.class })
public interface MarketplaceService {
    
    public Marketplace findMarketplaceByName(String name);

	public abstract long countAllMarketplaces();


	public abstract void deleteMarketplace(Marketplace marketplace);


	public abstract Marketplace findMarketplace(Long id);


	public abstract List<Marketplace> findAllMarketplaces();


	public abstract List<Marketplace> findMarketplaceEntries(int firstResult, int maxResults);


	public abstract void saveMarketplace(Marketplace marketplace);


	public abstract Marketplace updateMarketplace(Marketplace marketplace);

}
