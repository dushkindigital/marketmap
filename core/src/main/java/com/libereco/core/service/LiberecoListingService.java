package com.libereco.core.service;

import java.util.List;

import com.libereco.core.domain.LiberecoListing;

public interface LiberecoListingService {

    public abstract long countAllLiberecoListings();

    public abstract void deleteLiberecoListing(LiberecoListing liberecoListing);

    public abstract LiberecoListing findLiberecoListing(Long id);

    public abstract List<LiberecoListing> findAllLiberecoListings();

    public abstract List<LiberecoListing> findLiberecoListingEntries(int firstResult, int maxResults);

    public abstract void saveLiberecoListing(LiberecoListing liberecoListing);

    public abstract LiberecoListing updateLiberecoListing(LiberecoListing liberecoListing);
    
    public abstract List<LiberecoListing> findAllLiberecoListings(Long userId);

    public abstract List<LiberecoListing> findLiberecoListingEntries(Long userId, int firstResult, int maxResults);

    

}
