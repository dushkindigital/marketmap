package com.libereco.core.repository;

import com.libereco.core.domain.EbayListing;
import org.springframework.roo.addon.layers.repository.jpa.RooJpaRepository;

@RooJpaRepository(domainType = EbayListing.class)
public interface EbayListingRepository {
}
