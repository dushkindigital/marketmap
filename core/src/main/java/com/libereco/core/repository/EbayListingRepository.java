package com.libereco.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.libereco.core.domain.EbayListing;

@Repository
public interface EbayListingRepository extends JpaSpecificationExecutor<EbayListing>, JpaRepository<EbayListing, Long> {
}
