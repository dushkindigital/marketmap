package com.libereco.core.service;

import com.libereco.core.domain.ShippingInformation;
import com.libereco.core.repository.ShippingInformationRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ShippingInformationServiceImpl implements ShippingInformationService {

    @Autowired
    ShippingInformationRepository shippingInformationRepository;

    public long countAllShippingInformations() {
        return shippingInformationRepository.count();
    }

    public void deleteShippingInformation(ShippingInformation shippingInformation) {
        shippingInformationRepository.delete(shippingInformation);
    }

    public ShippingInformation findShippingInformation(Long id) {
        return shippingInformationRepository.findOne(id);
    }

    public List<ShippingInformation> findAllShippingInformations() {
        return shippingInformationRepository.findAll();
    }

    public List<ShippingInformation> findShippingInformationEntries(int firstResult, int maxResults) {
        return shippingInformationRepository.findAll(new org.springframework.data.domain.PageRequest(firstResult / maxResults, maxResults))
                .getContent();
    }

    public void saveShippingInformation(ShippingInformation shippingInformation) {
        shippingInformationRepository.save(shippingInformation);
    }

    public ShippingInformation updateShippingInformation(ShippingInformation shippingInformation) {
        return shippingInformationRepository.save(shippingInformation);
    }
}
