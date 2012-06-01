package com.libereco.core.repository;

import com.libereco.core.domain.LiberecoListing;
import org.springframework.roo.addon.layers.repository.jpa.RooJpaRepository;

@RooJpaRepository(domainType = LiberecoListing.class)
public interface LiberecoListingRepository {
}
