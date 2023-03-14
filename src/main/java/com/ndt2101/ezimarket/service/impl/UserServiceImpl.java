package com.ndt2101.ezimarket.service.impl;

import com.ndt2101.ezimarket.constant.Common;
import com.ndt2101.ezimarket.dto.AddressDTO;
import com.ndt2101.ezimarket.dto.CurrentDeviceDTO;
import com.ndt2101.ezimarket.dto.PasswordChangeDTO;
import com.ndt2101.ezimarket.exception.ApplicationException;
import com.ndt2101.ezimarket.exception.NotFoundException;
import com.ndt2101.ezimarket.model.*;
import com.ndt2101.ezimarket.repository.*;
import com.ndt2101.ezimarket.service.UserService;
import lombok.extern.java.Log;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private ProvinceRepository provinceRepository;
    @Autowired
    private DistrictRepository districtRepository;
    @Autowired
    private WardRepository wardRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ModelMapper modelMapper;

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

    @Override
    public String setLocation(AddressDTO addressDTO, String loginName) {
        UserLoginDataEntity userLoginData = userRepository.findByLoginName(loginName).orElseThrow(() -> new NotFoundException("User " + loginName + " not found"));
        AddressEntity addressEntity = modelMapper.map(addressDTO, AddressEntity.class);
        if (userLoginData.getAddress() != null) {
            addressEntity.setId(userLoginData.getAddress().getId());
        }
        Optional<ProvinceEntity> provinceEntity = provinceRepository.findById(addressDTO.getProvince().getProvinceID());
        if (provinceEntity.isPresent()) {
            addressEntity.setProvince(provinceEntity.get());
        }
        Optional<DistrictEntity> districtEntity = districtRepository.findById(addressDTO.getDistrict().getDistrictID());
        if (districtEntity.isPresent()) {
            addressEntity.setDistrict(districtEntity.get());
        }
        Optional<WardEntity> wardEntity = wardRepository.findById(addressDTO.getWard().getWardCode());
        if (wardEntity.isPresent()) {
            addressEntity.setWard(wardEntity.get());
        }
        addressEntity = addressRepository.save(addressEntity);
        userLoginData.setAddress(addressEntity);
        userRepository.save(userLoginData);
        return "Save address successfully";
    }

    @Override
    public String setCurrentDevice(CurrentDeviceDTO currentDeviceDTO) {
        UserLoginDataEntity userLoginDataEntity = userRepository.findById(currentDeviceDTO.getUserId()).orElseThrow(Common.userNotFound);
        userLoginDataEntity.setCurrentDevice(currentDeviceDTO.getToken());
        userLoginDataEntity = userRepository.save(userLoginDataEntity);
        return userLoginDataEntity.getCurrentDevice();
    }

    @Override
    public AddressDTO getLocation(String loginName) {
        UserLoginDataEntity userLoginData = userRepository.findByLoginName(loginName).orElseThrow(() -> new NotFoundException("User " + loginName + " not found"));
        if (userLoginData.getAddress() != null) {
            return modelMapper.map(userLoginData.getAddress(), AddressDTO.class);
        } else {
            return null;
        }
    }

    @Override
    public String getCurrentDevice(Long userId) {
        return userRepository.findById(userId).orElseThrow(Common.userNotFound).getCurrentDevice();
    }


}
