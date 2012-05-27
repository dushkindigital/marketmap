package com.libereco.core.service;

import com.libereco.core.domain.LiberecoShippingMethod;
import java.util.List;
import org.springframework.roo.addon.layers.service.RooService;

@RooService(domainTypes = { com.libereco.core.domain.LiberecoShippingMethod.class })
public interface LiberecoShippingMethodService {

	public abstract long countAllLiberecoShippingMethods();


	public abstract void deleteLiberecoShippingMethod(LiberecoShippingMethod liberecoShippingMethod);


	public abstract LiberecoShippingMethod findLiberecoShippingMethod(Long id);


	public abstract List<LiberecoShippingMethod> findAllLiberecoShippingMethods();


	public abstract List<LiberecoShippingMethod> findLiberecoShippingMethodEntries(int firstResult, int maxResults);


	public abstract void saveLiberecoShippingMethod(LiberecoShippingMethod liberecoShippingMethod);


	public abstract LiberecoShippingMethod updateLiberecoShippingMethod(LiberecoShippingMethod liberecoShippingMethod);

}
