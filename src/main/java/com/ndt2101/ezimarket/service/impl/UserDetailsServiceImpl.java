package com.ndt2101.ezimarket.service.impl;

import com.ndt2101.ezimarket.exception.NotFoundException;
import com.ndt2101.ezimarket.model.UserLoginDataEntity;
import com.ndt2101.ezimarket.repository.UserRepository;
import com.ndt2101.ezimarket.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String loginName) throws AuthenticationException {
        UserLoginDataEntity userLoginData = userRepository.findByLoginName(loginName)
                .orElseThrow(() -> new InternalAuthenticationServiceException("Login name or password is incorrect!"));
        return UserPrincipal.create(userLoginData);
    }
}
