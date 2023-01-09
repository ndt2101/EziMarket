package com.ndt2101.ezimarket.service;

import com.ndt2101.ezimarket.dto.ShopDTO;

public interface ShopService {
    String register(ShopDTO shopDTO, String loginName);
}
