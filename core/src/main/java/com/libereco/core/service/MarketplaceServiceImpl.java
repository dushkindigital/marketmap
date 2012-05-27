package com.libereco.core.service;

import com.libereco.core.domain.Marketplace;
import com.libereco.core.repository.MarketplaceRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class MarketplaceServiceImpl implements MarketplaceService {
    
    public Marketplace findMarketplaceByName(String name){
        return marketplaceRepository.findByMarketplaceName(name);
    }

	@Autowired
    MarketplaceRepository marketplaceRepository;

	public long countAllMarketplaces() {
        return marketplaceRepository.count();
    }

	public void deleteMarketplace(Marketplace marketplace) {
        marketplaceRepository.delete(marketplace);
    }

	public Marketplace findMarketplace(Long id) {
        return marketplaceRepository.findOne(id);
    }

	public List<Marketplace> findAllMarketplaces() {
        return marketplaceRepository.findAll();
    }

	public List<Marketplace> findMarketplaceEntries(int firstResult, int maxResults) {
        return marketplaceRepository.findAll(new org.springframework.data.domain.PageRequest(firstResult / maxResults, maxResults)).getContent();
    }

	public void saveMarketplace(Marketplace marketplace) {
        marketplaceRepository.save(marketplace);
    }

	public Marketplace updateMarketplace(Marketplace marketplace) {
        return marketplaceRepository.save(marketplace);
    }
}
