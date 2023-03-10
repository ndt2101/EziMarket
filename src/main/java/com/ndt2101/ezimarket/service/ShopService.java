package com.ndt2101.ezimarket.service;

import com.ndt2101.ezimarket.dto.ShopDTO;
import com.ndt2101.ezimarket.exception.ApplicationException;
import com.ndt2101.ezimarket.model.ShopEntity;
import org.springframework.data.jpa.domain.Specification;

import java.util.concurrent.ExecutionException;

public interface ShopService {
    String register(ShopDTO shopDTO, String loginName) throws ExecutionException, InterruptedException, ApplicationException;
    ShopDTO getShop(Specification<ShopEntity> specification);
}
