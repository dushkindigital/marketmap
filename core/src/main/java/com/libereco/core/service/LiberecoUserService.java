package com.libereco.core.service;

import java.util.List;
import org.springframework.roo.addon.layers.service.RooService;

import com.libereco.core.domain.LiberecoUser;

@RooService(domainTypes = { com.libereco.core.domain.LiberecoUser.class })
public interface LiberecoUserService {

    public LiberecoUser findUserByUsername(String username);

    public abstract long countAllLiberecoUsers();

    public abstract void deleteLiberecoUser(LiberecoUser liberecoUser);

    public abstract LiberecoUser findLiberecoUser(Long id);

    public abstract List<LiberecoUser> findAllLiberecoUsers();

    public abstract List<LiberecoUser> findLiberecoUserEntries(int firstResult, int maxResults);

    public abstract void saveLiberecoUser(LiberecoUser liberecoUser);

    public abstract LiberecoUser updateLiberecoUser(LiberecoUser liberecoUser);

    public LiberecoUser findUserbyUsernameAndPassword(String username, String password);

}
