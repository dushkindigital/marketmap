package com.libereco.core.service;

import java.util.List;

import com.libereco.core.domain.EtsyListing;
import com.libereco.core.domain.LiberecoListing;

public interface EtsyListingService {

    public abstract long countAllEtsyListings();

    public abstract void deleteEtsyListing(EtsyListing etsyListing);

    public abstract EtsyListing findEtsyListing(Long id);

    public abstract List<EtsyListing> findAllEtsyListings();

    public abstract List<EtsyListing> findEtsyListingEntries(int firstResult, int maxResults);

    public abstract void saveEtsyListing(EtsyListing etsyListing);

    public abstract EtsyListing updateEtsyListing(EtsyListing etsyListing);

    public abstract List<EtsyListing> findAllEtsyListings(Long userId);

    public abstract List<EtsyListing> findEtsyListingEntries(Long userId, int firstResult, int maxResults);

    public abstract EtsyListing findEtsyListing(LiberecoListing liberecoListing);

}
