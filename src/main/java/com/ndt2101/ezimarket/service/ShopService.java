package com.ndt2101.ezimarket.service;

import com.ndt2101.ezimarket.dto.ShopDTO;
import com.ndt2101.ezimarket.model.ShopEntity;
import org.springframework.data.jpa.domain.Specification;

public interface ShopService {
    String register(ShopDTO shopDTO, String loginName);
    ShopDTO getShop(Specification<ShopEntity> specification);
}
