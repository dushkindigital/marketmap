package com.libereco.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.libereco.core.domain.LiberecoUser;

@Repository
public interface LiberecoUserRepository extends JpaRepository<LiberecoUser, Long>, JpaSpecificationExecutor<LiberecoUser> {

    public LiberecoUser findByUsername(String username);

    public LiberecoUser findByUsernameAndPassword(String username, String password);
}
