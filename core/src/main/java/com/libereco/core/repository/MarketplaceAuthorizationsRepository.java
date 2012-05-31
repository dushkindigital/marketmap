package com.libereco.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.libereco.core.domain.MarketplaceAuthorizations;
import com.libereco.core.domain.MarketplaceAuthorizationsCompositeKey;

@Repository
public interface MarketplaceAuthorizationsRepository extends JpaRepository<MarketplaceAuthorizations, MarketplaceAuthorizationsCompositeKey>,
        JpaSpecificationExecutor<MarketplaceAuthorizations> {
    @Query("select count(*) from MarketplaceAuthorizations ma")
    public long countMarketplaceAuthorizations();

    public List<MarketplaceAuthorizations> findAllMarketplaceAuthorizationsBykeyUserId(Long id);
}
