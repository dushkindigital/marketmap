package com.libereco.core.service;

import java.util.List;

import com.libereco.core.domain.MergedLiberecoListing;

public interface MergedLiberecoListingService {

    public abstract long countAllLiberecoListings();

    public abstract void deleteLiberecoListing(MergedLiberecoListing liberecoListing);

    public abstract MergedLiberecoListing findLiberecoListing(Long id);

    public abstract List<MergedLiberecoListing> findAllLiberecoListings();

    public abstract List<MergedLiberecoListing> findLiberecoListingEntries(int firstResult, int maxResults);

    public abstract void saveLiberecoListing(MergedLiberecoListing liberecoListing);

    public abstract MergedLiberecoListing updateLiberecoListing(MergedLiberecoListing liberecoListing);

    public abstract List<MergedLiberecoListing> findAllLiberecoListings(Long userId);

    public abstract List<MergedLiberecoListing> findLiberecoListingEntries(Long userId, int firstResult, int maxResults);

    public abstract List<MergedLiberecoListing> findAllNotListedListingsForUser(Long id, String marketplaceName);

}
