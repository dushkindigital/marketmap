package com.libereco.core.repository;

import com.libereco.core.domain.LiberecoPaymentInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.roo.addon.layers.repository.jpa.RooJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@RooJpaRepository(domainType = LiberecoPaymentInformation.class)
public interface LiberecoPaymentInformationRepository extends JpaRepository<LiberecoPaymentInformation, Long>, JpaSpecificationExecutor<LiberecoPaymentInformation> {
}
