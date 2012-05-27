package com.libereco.core.service;

import com.libereco.core.domain.MarketplaceAuthorizations;
import com.libereco.core.domain.MarketplaceAuthorizationsCompositeKey;
import com.libereco.core.repository.MarketplaceAuthorizationsRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class MarketplaceAuthorizationsServiceImpl implements MarketplaceAuthorizationsService {

	@Autowired
    MarketplaceAuthorizationsRepository marketplaceAuthorizationsRepository;

	public long countAllMarketplaceAuthorizationses() {
        return marketplaceAuthorizationsRepository.count();
    }

	public void deleteMarketplaceAuthorizations(MarketplaceAuthorizations marketplaceAuthorizations) {
        marketplaceAuthorizationsRepository.delete(marketplaceAuthorizations);
    }

	public MarketplaceAuthorizations findMarketplaceAuthorizations(MarketplaceAuthorizationsCompositeKey id) {
        return marketplaceAuthorizationsRepository.findOne(id);
    }

	public List<MarketplaceAuthorizations> findAllMarketplaceAuthorizationses() {
        return marketplaceAuthorizationsRepository.findAll();
    }

	public List<MarketplaceAuthorizations> findMarketplaceAuthorizationsEntries(int firstResult, int maxResults) {
        return marketplaceAuthorizationsRepository.findAll(new org.springframework.data.domain.PageRequest(firstResult / maxResults, maxResults)).getContent();
    }

	public void saveMarketplaceAuthorizations(MarketplaceAuthorizations marketplaceAuthorizations) {
        marketplaceAuthorizationsRepository.save(marketplaceAuthorizations);
    }

	public MarketplaceAuthorizations updateMarketplaceAuthorizations(MarketplaceAuthorizations marketplaceAuthorizations) {
        return marketplaceAuthorizationsRepository.save(marketplaceAuthorizations);
    }
}
