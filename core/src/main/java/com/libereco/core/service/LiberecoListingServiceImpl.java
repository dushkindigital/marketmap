package com.libereco.core.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.libereco.core.domain.LiberecoListing;
import com.libereco.core.domain.Marketplace;
import com.libereco.core.repository.LiberecoListingRepository;

@Service
@Transactional
public class LiberecoListingServiceImpl implements LiberecoListingService {

    @Autowired
    LiberecoListingRepository liberecoListingRepository;

    @Autowired
    LiberecoUserService liberecoUserService;

    public long countAllLiberecoListings() {
        return liberecoListingRepository.count();
    }

    public void deleteLiberecoListing(LiberecoListing liberecoListing) {
        liberecoListingRepository.delete(liberecoListing);
    }

    public LiberecoListing findLiberecoListing(Long id) {
        return liberecoListingRepository.findOne(id);
    }

    public List<LiberecoListing> findAllLiberecoListings() {
        return liberecoListingRepository.findAll();
    }

    public List<LiberecoListing> findLiberecoListingEntries(int firstResult, int maxResults) {
        return liberecoListingRepository.findAll(new org.springframework.data.domain.PageRequest(firstResult / maxResults, maxResults)).getContent();
    }

    public void saveLiberecoListing(LiberecoListing liberecoListing) {
        liberecoListingRepository.save(liberecoListing);
    }

    public LiberecoListing updateLiberecoListing(LiberecoListing liberecoListing) {
        return liberecoListingRepository.save(liberecoListing);
    }

    @Override
    public List<LiberecoListing> findAllLiberecoListings(Long userId) {
        return liberecoListingRepository.findAllListingByUserId(userId);
    }

    @Override
    public List<LiberecoListing> findLiberecoListingEntries(Long userId, int firstResult, int maxResults) {
        return liberecoListingRepository.findListingsByUserId(userId,
                new org.springframework.data.domain.PageRequest(firstResult / maxResults, maxResults)).getContent();
    }

    @Override
    public List<LiberecoListing> findAllNotListedListingsForUser(Long userId, String marketplaceName) {
        List<LiberecoListing> listings = liberecoListingRepository.findAllListingByUserId(userId);
        List<LiberecoListing> marketplaceListings = new ArrayList<LiberecoListing>(listings);
        for (LiberecoListing liberecoListing : listings) {
            for (Marketplace marketplace : liberecoListing.getMarketplaces()) {
                if(StringUtils.equalsIgnoreCase(marketplaceName, marketplace.getMarketplaceName())){
                    marketplaceListings.remove(liberecoListing);
                }
                
            }
        }
        return marketplaceListings;
    }
}
