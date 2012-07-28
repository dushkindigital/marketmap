package com.libereco.core.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.libereco.core.domain.EtsyListing;
import com.libereco.core.domain.LiberecoListing;

@Repository
public interface EtsyListingRepository extends JpaSpecificationExecutor<EtsyListing>, JpaRepository<EtsyListing, Long> {

    List<EtsyListing> findAllEtsyListingByLiberecoListing_UserId(Long userId);

    Page<EtsyListing> findAllEtsyListingByLiberecoListing_UserId(Long userId, Pageable pageable);

    EtsyListing findByLiberecoListing(LiberecoListing liberecoListing);
}
