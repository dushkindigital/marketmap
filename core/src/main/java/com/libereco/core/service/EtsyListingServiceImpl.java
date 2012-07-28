package com.libereco.core.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.libereco.core.domain.EtsyListing;
import com.libereco.core.domain.LiberecoListing;
import com.libereco.core.repository.EtsyListingRepository;

@Service
@Transactional
public class EtsyListingServiceImpl implements EtsyListingService {

    @Autowired
    EtsyListingRepository etsyListingRepository;

    public long countAllEtsyListings() {
        return etsyListingRepository.count();
    }

    public void deleteEtsyListing(EtsyListing etsyListing) {
        etsyListingRepository.delete(etsyListing);
    }

    public EtsyListing findEtsyListing(Long id) {
        return etsyListingRepository.findOne(id);
    }

    public List<EtsyListing> findAllEtsyListings() {
        return etsyListingRepository.findAll();
    }

    public List<EtsyListing> findEtsyListingEntries(int firstResult, int maxResults) {
        return etsyListingRepository.findAll(new org.springframework.data.domain.PageRequest(firstResult / maxResults, maxResults)).getContent();
    }

    public void saveEtsyListing(EtsyListing etsyListing) {
        etsyListingRepository.save(etsyListing);
    }

    public EtsyListing updateEtsyListing(EtsyListing etsyListing) {
        return etsyListingRepository.save(etsyListing);
    }

    @Override
    public List<EtsyListing> findAllEtsyListings(Long userId) {
        return etsyListingRepository.findAllEtsyListingByLiberecoListing_UserId(userId);
    }

    @Override
    public List<EtsyListing> findEtsyListingEntries(Long userId, int firstResult, int maxResults) {
        return etsyListingRepository.findAllEtsyListingByLiberecoListing_UserId(userId,
                new org.springframework.data.domain.PageRequest(firstResult / maxResults, maxResults)).getContent();
    }

    @Override
    public EtsyListing findEtsyListing(LiberecoListing liberecoListing) {
        return etsyListingRepository.findByLiberecoListing(liberecoListing);
    }
}
