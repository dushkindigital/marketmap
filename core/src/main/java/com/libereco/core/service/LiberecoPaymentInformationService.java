package com.libereco.core.service;

import com.libereco.core.domain.LiberecoPaymentInformation;
import java.util.List;
import org.springframework.roo.addon.layers.service.RooService;

@RooService(domainTypes = { com.libereco.core.domain.LiberecoPaymentInformation.class })
public interface LiberecoPaymentInformationService {

	public abstract long countAllLiberecoPaymentInformations();


	public abstract void deleteLiberecoPaymentInformation(LiberecoPaymentInformation liberecoPaymentInformation);


	public abstract LiberecoPaymentInformation findLiberecoPaymentInformation(Long id);


	public abstract List<LiberecoPaymentInformation> findAllLiberecoPaymentInformations();


	public abstract List<LiberecoPaymentInformation> findLiberecoPaymentInformationEntries(int firstResult, int maxResults);


	public abstract void saveLiberecoPaymentInformation(LiberecoPaymentInformation liberecoPaymentInformation);


	public abstract LiberecoPaymentInformation updateLiberecoPaymentInformation(LiberecoPaymentInformation liberecoPaymentInformation);

}
