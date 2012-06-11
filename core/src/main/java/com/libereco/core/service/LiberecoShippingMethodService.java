package com.libereco.core.service;

import java.util.List;

import com.libereco.core.domain.LiberecoShippingMethod;

public interface LiberecoShippingMethodService {

    public abstract long countAllLiberecoShippingMethods();

    public abstract void deleteLiberecoShippingMethod(LiberecoShippingMethod liberecoShippingMethod);

    public abstract LiberecoShippingMethod findLiberecoShippingMethod(Long id);

    public abstract List<LiberecoShippingMethod> findAllLiberecoShippingMethods();

    public abstract List<LiberecoShippingMethod> findLiberecoShippingMethodEntries(int firstResult, int maxResults);

    public abstract void saveLiberecoShippingMethod(LiberecoShippingMethod liberecoShippingMethod);

    public abstract LiberecoShippingMethod updateLiberecoShippingMethod(LiberecoShippingMethod liberecoShippingMethod);

}
