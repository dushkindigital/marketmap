package com.libereco.core.service;

import com.libereco.core.domain.LiberecoShippingMethod;
import com.libereco.core.repository.LiberecoShippingMethodRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class LiberecoShippingMethodServiceImpl implements LiberecoShippingMethodService {

	@Autowired
    LiberecoShippingMethodRepository liberecoShippingMethorRepository;

	public long countAllLiberecoShippingMethods() {
        return liberecoShippingMethorRepository.count();
    }

	public void deleteLiberecoShippingMethod(LiberecoShippingMethod liberecoShippingMethod) {
        liberecoShippingMethorRepository.delete(liberecoShippingMethod);
    }

	public LiberecoShippingMethod findLiberecoShippingMethod(Long id) {
        return liberecoShippingMethorRepository.findOne(id);
    }

	public List<LiberecoShippingMethod> findAllLiberecoShippingMethods() {
        return liberecoShippingMethorRepository.findAll();
    }

	public List<LiberecoShippingMethod> findLiberecoShippingMethodEntries(int firstResult, int maxResults) {
        return liberecoShippingMethorRepository.findAll(new org.springframework.data.domain.PageRequest(firstResult / maxResults, maxResults)).getContent();
    }

	public void saveLiberecoShippingMethod(LiberecoShippingMethod liberecoShippingMethod) {
        liberecoShippingMethorRepository.save(liberecoShippingMethod);
    }

	public LiberecoShippingMethod updateLiberecoShippingMethod(LiberecoShippingMethod liberecoShippingMethod) {
        return liberecoShippingMethorRepository.save(liberecoShippingMethod);
    }
}
