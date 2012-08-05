package com.libereco.core.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.libereco.core.domain.MergedLiberecoListing;

@Repository
public interface MergedLiberecoListingRepository extends JpaSpecificationExecutor<MergedLiberecoListing>, JpaRepository<MergedLiberecoListing, Long> {

    List<MergedLiberecoListing> findAllListingByUserId(Long userId);
    
    Page<MergedLiberecoListing> findListingsByUserId(Long userId, Pageable pageable);

    List<MergedLiberecoListing> findAllListingsByUserIdAndMarketplaces_MarketplaceNameNot(Long userId, String marketplace);
}
