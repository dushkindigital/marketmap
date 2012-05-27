package com.libereco.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.libereco.core.domain.LiberecoShippingMethod;

@Repository
public interface LiberecoShippingMethorRepository extends JpaRepository<LiberecoShippingMethod, Long>, JpaSpecificationExecutor<LiberecoShippingMethod> {
}
