package com.ndt2101.ezimarket.service.impl;

import com.ndt2101.ezimarket.dto.ShopDTO;
import com.ndt2101.ezimarket.exception.NotFoundException;
import com.ndt2101.ezimarket.model.ShopEntity;
import com.ndt2101.ezimarket.model.UserLoginDataEntity;
import com.ndt2101.ezimarket.repository.ShopRepository;
import com.ndt2101.ezimarket.repository.UserRepository;
import com.ndt2101.ezimarket.service.ShopService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShopServiceImpl implements ShopService {

    @Autowired
    private ShopRepository shopRepository;
    @Autowired
    private ModelMapper mapper;
    @Autowired
    private UserRepository userRepository;
    @Override
    public String register(ShopDTO shopDTO, String loginName) {
        UserLoginDataEntity userLoginData = userRepository.findByLoginName(loginName).orElseThrow(() -> new NotFoundException("User " + loginName + " not found"));
        ShopEntity shopEntity = mapper.map(shopDTO, ShopEntity.class);
        shopEntity.setUserLoginData(userLoginData);
        shopRepository.save(shopEntity);
        return "Register shop successfully";
    }
}
