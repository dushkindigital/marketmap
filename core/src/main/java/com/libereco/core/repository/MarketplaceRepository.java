package com.libereco.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.libereco.core.domain.Marketplace;

@Repository
public interface MarketplaceRepository extends JpaSpecificationExecutor<Marketplace>, JpaRepository<Marketplace, Long> {
    
    public Marketplace findByMarketplaceName(String marketplaceName);
}
