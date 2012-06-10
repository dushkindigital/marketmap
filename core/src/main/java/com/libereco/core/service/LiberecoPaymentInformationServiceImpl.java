package com.libereco.core.service;

import com.libereco.core.domain.LiberecoPaymentInformation;
import com.libereco.core.repository.LiberecoPaymentInformationRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LiberecoPaymentInformationServiceImpl implements LiberecoPaymentInformationService {

    @Autowired
    LiberecoPaymentInformationRepository liberecoPaymentInformationRepository;

    public long countAllLiberecoPaymentInformations() {
        return liberecoPaymentInformationRepository.count();
    }

    public void deleteLiberecoPaymentInformation(LiberecoPaymentInformation liberecoPaymentInformation) {
        liberecoPaymentInformationRepository.delete(liberecoPaymentInformation);
    }

    public LiberecoPaymentInformation findLiberecoPaymentInformation(Long id) {
        return liberecoPaymentInformationRepository.findOne(id);
    }

    public List<LiberecoPaymentInformation> findAllLiberecoPaymentInformations() {
        return liberecoPaymentInformationRepository.findAll();
    }

    public List<LiberecoPaymentInformation> findLiberecoPaymentInformationEntries(int firstResult, int maxResults) {
        return liberecoPaymentInformationRepository.findAll(new org.springframework.data.domain.PageRequest(firstResult / maxResults, maxResults))
                .getContent();
    }

    public void saveLiberecoPaymentInformation(LiberecoPaymentInformation liberecoPaymentInformation) {
        liberecoPaymentInformationRepository.save(liberecoPaymentInformation);
    }

    public LiberecoPaymentInformation updateLiberecoPaymentInformation(LiberecoPaymentInformation liberecoPaymentInformation) {
        return liberecoPaymentInformationRepository.save(liberecoPaymentInformation);
    }
}
