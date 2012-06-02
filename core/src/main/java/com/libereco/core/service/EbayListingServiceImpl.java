package com.libereco.core.service;

import com.libereco.core.domain.EbayListing;
import com.libereco.core.repository.EbayListingRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EbayListingServiceImpl implements EbayListingService {

    @Autowired
    EbayListingRepository ebayListingRepository;

    public long countAllEbayListings() {
        return ebayListingRepository.count();
    }

    public void deleteEbayListing(EbayListing ebayListing) {
        ebayListingRepository.delete(ebayListing);
    }

    public EbayListing findEbayListing(Long id) {
        return ebayListingRepository.findOne(id);
    }

    public List<EbayListing> findAllEbayListings() {
        return ebayListingRepository.findAll();
    }

    public List<EbayListing> findEbayListingEntries(int firstResult, int maxResults) {
        return ebayListingRepository.findAll(new org.springframework.data.domain.PageRequest(firstResult / maxResults, maxResults)).getContent();
    }

    public void saveEbayListing(EbayListing ebayListing) {
        ebayListingRepository.save(ebayListing);
    }

    public EbayListing updateEbayListing(EbayListing ebayListing) {
        return ebayListingRepository.save(ebayListing);
    }
}
