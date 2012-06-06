package com.libereco.core.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.libereco.core.domain.LiberecoListing;

@Repository
public interface LiberecoListingRepository extends JpaSpecificationExecutor<LiberecoListing>, JpaRepository<LiberecoListing, Long> {

    List<LiberecoListing> findAllListingByUserId(Long userId);
    
    Page<LiberecoListing> findListingsByUserId(Long userId, Pageable pageable);

    List<LiberecoListing> findAllListingsByUserIdAndMarketplaces_MarketplaceNameNot(Long userId, String marketplace);
}
