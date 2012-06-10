package com.libereco.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.libereco.core.domain.ShippingInformation;

@Repository
public interface ShippingInformationRepository extends JpaRepository<ShippingInformation, Long>, JpaSpecificationExecutor<ShippingInformation> {
}
