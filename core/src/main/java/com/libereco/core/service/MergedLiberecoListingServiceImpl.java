package com.libereco.core.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.libereco.core.domain.Marketplace;
import com.libereco.core.domain.MergedLiberecoListing;
import com.libereco.core.repository.MergedLiberecoListingRepository;

@Service
@Transactional
public class MergedLiberecoListingServiceImpl implements MergedLiberecoListingService {

    @Autowired
    MergedLiberecoListingRepository mergedLiberecoListingRepository;

    @Autowired
    LiberecoUserService liberecoUserService;

    public long countAllLiberecoListings() {
        return mergedLiberecoListingRepository.count();
    }

    public void deleteLiberecoListing(MergedLiberecoListing liberecoListing) {
        mergedLiberecoListingRepository.delete(liberecoListing);
    }

    public MergedLiberecoListing findLiberecoListing(Long id) {
        return mergedLiberecoListingRepository.findOne(id);
    }

    public List<MergedLiberecoListing> findAllLiberecoListings() {
        return mergedLiberecoListingRepository.findAll();
    }

    public List<MergedLiberecoListing> findLiberecoListingEntries(int firstResult, int maxResults) {
        return mergedLiberecoListingRepository.findAll(new org.springframework.data.domain.PageRequest(firstResult / maxResults, maxResults)).getContent();
    }

    public void saveLiberecoListing(MergedLiberecoListing liberecoListing) {
        mergedLiberecoListingRepository.save(liberecoListing);
    }

    public MergedLiberecoListing updateLiberecoListing(MergedLiberecoListing liberecoListing) {
        return mergedLiberecoListingRepository.save(liberecoListing);
    }

    @Override
    public List<MergedLiberecoListing> findAllLiberecoListings(Long userId) {
        return mergedLiberecoListingRepository.findAllListingByUserId(userId);
    }

    @Override
    public List<MergedLiberecoListing> findLiberecoListingEntries(Long userId, int firstResult, int maxResults) {
        return mergedLiberecoListingRepository.findListingsByUserId(userId,
                new org.springframework.data.domain.PageRequest(firstResult / maxResults, maxResults)).getContent();
    }

    @Override
    public List<MergedLiberecoListing> findAllNotListedListingsForUser(Long userId, String marketplaceName) {
        List<MergedLiberecoListing> listings = mergedLiberecoListingRepository.findAllListingByUserId(userId);
        List<MergedLiberecoListing> marketplaceListings = new ArrayList<MergedLiberecoListing>(listings);
        for (MergedLiberecoListing liberecoListing : listings) {
            for (Marketplace marketplace : liberecoListing.getMarketplaces()) {
                if(StringUtils.equalsIgnoreCase(marketplaceName, marketplace.getMarketplaceName())){
                    marketplaceListings.remove(liberecoListing);
                }
                
            }
        }
        return marketplaceListings;
    }
}
