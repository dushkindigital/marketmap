package com.libereco.core.service;

import com.libereco.core.domain.LiberecoUser;
import com.libereco.core.repository.LiberecoUserRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class LiberecoUserServiceImpl implements LiberecoUserService {
    
    public LiberecoUser findUserByUsername(String username){
        return liberecoUserRepository.findByUsername(username);
    }

	@Autowired
    LiberecoUserRepository liberecoUserRepository;

	public long countAllLiberecoUsers() {
        return liberecoUserRepository.count();
    }

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

	public LiberecoUser updateLiberecoUser(LiberecoUser liberecoUser) {
        return liberecoUserRepository.save(liberecoUser);
    }
}
