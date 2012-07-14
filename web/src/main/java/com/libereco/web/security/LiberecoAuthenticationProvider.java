package com.libereco.web.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.libereco.core.domain.LiberecoUser;
import com.libereco.core.exceptions.GenericLiberecoException;
import com.libereco.core.service.LiberecoUserService;

@Component
public class LiberecoAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    @Autowired
    private LiberecoUserService liberecoUserService;

    @Override
    protected void additionalAuthenticationChecks(UserDetails arg0, UsernamePasswordAuthenticationToken arg1) throws AuthenticationException {

    }

    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authenticationToken) throws AuthenticationException {
        String password = (String) authenticationToken.getCredentials();
        if (!StringUtils.hasText(password)) {
            throw new BadCredentialsException("Please enter password");
        }
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        try {
            LiberecoUser user = liberecoUserService.findUserbyUsernameAndPassword(username, password);
            if (user == null) {
                throw new GenericLiberecoException("Username or password incorrect." + username);
            }
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        } catch (Exception e) {
            throw new BadCredentialsException("Non-unique user, contact administrator");
        }
        return new User(username, password, true, // enabled
                true, // account not expired
                true, // credentials not expired
                true, // account not locked
                authorities);
    }

}
