package com.libereco.core.service;

import com.libereco.core.domain.LiberecoShippingInformation;
import com.libereco.core.repository.LiberecoShippingInformationRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LiberecoShippingInformationServiceImpl implements LiberecoShippingInformationService {

    @Autowired
    LiberecoShippingInformationRepository liberecoShippingInformationRepository;

    public long countAllShippingInformations() {
        return liberecoShippingInformationRepository.count();
    }

    public void deleteShippingInformation(LiberecoShippingInformation shippingInformation) {
        liberecoShippingInformationRepository.delete(shippingInformation);
    }

    public LiberecoShippingInformation findShippingInformation(Long id) {
        return liberecoShippingInformationRepository.findOne(id);
    }

    public List<LiberecoShippingInformation> findAllShippingInformations() {
        return liberecoShippingInformationRepository.findAll();
    }

    public List<LiberecoShippingInformation> findShippingInformationEntries(int firstResult, int maxResults) {
        return liberecoShippingInformationRepository.findAll(new org.springframework.data.domain.PageRequest(firstResult / maxResults, maxResults))
                .getContent();
    }

    public void saveShippingInformation(LiberecoShippingInformation shippingInformation) {
        liberecoShippingInformationRepository.save(shippingInformation);
    }

    public LiberecoShippingInformation updateShippingInformation(LiberecoShippingInformation shippingInformation) {
        return liberecoShippingInformationRepository.save(shippingInformation);
    }
}
