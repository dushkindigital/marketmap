package com.libereco.core.service;

import java.util.List;

import com.libereco.core.domain.ShippingInformation;

public interface ShippingInformationService {

    public abstract long countAllShippingInformations();

    public abstract void deleteShippingInformation(ShippingInformation shippingInformation);

    public abstract ShippingInformation findShippingInformation(Long id);

    public abstract List<ShippingInformation> findAllShippingInformations();

    public abstract List<ShippingInformation> findShippingInformationEntries(int firstResult, int maxResults);

    public abstract void saveShippingInformation(ShippingInformation shippingInformation);

    public abstract ShippingInformation updateShippingInformation(ShippingInformation shippingInformation);

}
