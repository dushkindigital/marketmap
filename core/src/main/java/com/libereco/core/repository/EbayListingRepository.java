package com.libereco.core.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.libereco.core.domain.EbayListing;
import com.libereco.core.domain.LiberecoListing;

@Repository
public interface EbayListingRepository extends JpaSpecificationExecutor<EbayListing>, JpaRepository<EbayListing, Long> {

    List<EbayListing> findAllEbayListingByLiberecoListing_UserId(Long userId);

    Page<EbayListing> findAllEbayListingByLiberecoListing_UserId(Long userId, Pageable pageable);

    EbayListing findByLiberecoListing(LiberecoListing liberecoListing);
}
