package com.libereco.core.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.libereco.core.domain.LiberecoUser;
import com.libereco.core.repository.LiberecoUserRepository;

@Service
@Transactional
public class LiberecoUserServiceImpl implements LiberecoUserService {

    @Autowired
    LiberecoUserRepository liberecoUserRepository;

    @Cacheable(value = "users", key = "#username")
    public LiberecoUser findUserByUsername(String username) {
        return liberecoUserRepository.findByUsername(username);
    }

    public long countAllLiberecoUsers() {
        return liberecoUserRepository.count();
    }

    @CacheEvict(value = "users", key = "#liberecoUser.username")
    public void deleteLiberecoUser(LiberecoUser liberecoUser) {
        liberecoUserRepository.delete(liberecoUser);
    }

    public LiberecoUser findLiberecoUser(Long id) {
        return liberecoUserRepository.findOne(id);
    }

    public List<LiberecoUser> findAllLiberecoUsers() {
        return liberecoUserRepository.findAll();
    }

    public List<LiberecoUser> findLiberecoUserEntries(int firstResult, int maxResults) {
        return liberecoUserRepository.findAll(new org.springframework.data.domain.PageRequest(firstResult / maxResults, maxResults)).getContent();
    }

    public void saveLiberecoUser(LiberecoUser liberecoUser) {
        liberecoUserRepository.save(liberecoUser);
    }

    @CacheEvict(value = "users", key = "#liberecoUser.username")
    public LiberecoUser updateLiberecoUser(LiberecoUser liberecoUser) {
        return liberecoUserRepository.save(liberecoUser);
    }

    @Override
    public LiberecoUser findUserbyUsernameAndPassword(String username, String password) {
        return liberecoUserRepository.findByUsernameAndPassword(username, password);
    }
}
