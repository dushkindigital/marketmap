package com.libereco.core.service;

import java.util.List;

import com.libereco.core.domain.EbayListing;

public interface EbayListingService {

    public abstract long countAllEbayListings();

    public abstract void deleteEbayListing(EbayListing ebayListing);

    public abstract EbayListing findEbayListing(Long id);

    public abstract List<EbayListing> findAllEbayListings();

    public abstract List<EbayListing> findEbayListingEntries(int firstResult, int maxResults);

    public abstract void saveEbayListing(EbayListing ebayListing);

    public abstract EbayListing updateEbayListing(EbayListing ebayListing);

}
