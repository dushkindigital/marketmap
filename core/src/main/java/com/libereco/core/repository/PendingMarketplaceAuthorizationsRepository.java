package com.libereco.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.libereco.core.domain.PendingMarketplaceAuthorizations;
import com.libereco.core.domain.UserMarketplaceKey;

@Repository
public interface PendingMarketplaceAuthorizationsRepository extends JpaRepository<PendingMarketplaceAuthorizations, UserMarketplaceKey> {

    @Query("select count(*) from PendingMarketplaceAuthorizations pa")
    public long countPendingMarketplaceAuthorizations();
}
