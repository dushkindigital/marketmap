package com.libereco.core.service;

import java.util.List;

import com.libereco.core.domain.LiberecoShippingInformation;

public interface LiberecoShippingInformationService {

    public abstract long countAllShippingInformations();

    public abstract void deleteShippingInformation(LiberecoShippingInformation shippingInformation);

    public abstract LiberecoShippingInformation findShippingInformation(Long id);

    public abstract List<LiberecoShippingInformation> findAllShippingInformations();

    public abstract List<LiberecoShippingInformation> findShippingInformationEntries(int firstResult, int maxResults);

    public abstract void saveShippingInformation(LiberecoShippingInformation shippingInformation);

    public abstract LiberecoShippingInformation updateShippingInformation(LiberecoShippingInformation shippingInformation);

}
