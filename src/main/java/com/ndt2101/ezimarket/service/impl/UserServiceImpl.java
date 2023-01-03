package com.ndt2101.ezimarket.service.impl;

import com.ndt2101.ezimarket.dto.PasswordChangeDTO;
import com.ndt2101.ezimarket.exception.ApplicationException;
import com.ndt2101.ezimarket.exception.NotFoundException;
import com.ndt2101.ezimarket.model.UserLoginDataEntity;
import com.ndt2101.ezimarket.repository.UserRepository;
import com.ndt2101.ezimarket.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public String passwordChange(PasswordChangeDTO passwordChangeDTO, String loginName) throws Exception {
        UserLoginDataEntity userLoginData = userRepository.findByLoginName(loginName).orElseThrow(() -> new NotFoundException("User " + loginName + " not found"));
        if (passwordEncoder.matches(passwordChangeDTO.getOldPassword(), userLoginData.getPassword())) {
            userLoginData.setPassword(passwordEncoder.encode(passwordChangeDTO.getNewPassword()));
            userRepository.save(userLoginData);
            return "Change password successfully";
        } else {
            throw new ApplicationException(HttpStatus.UNPROCESSABLE_ENTITY, "Change password unsuccessfully. Old password is incorrect");
        }
    }
}
