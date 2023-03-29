package com.ndt2101.ezimarket.service;

import com.ndt2101.ezimarket.dto.ShopDTO;
import com.ndt2101.ezimarket.dto.pagination.PaginateDTO;
import com.ndt2101.ezimarket.exception.ApplicationException;
import com.ndt2101.ezimarket.model.ShopEntity;
import com.ndt2101.ezimarket.specification.GenericSpecification;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface ShopService {
    String register(ShopDTO shopDTO, String loginName) throws ExecutionException, InterruptedException, ApplicationException;
    ShopDTO getShop(Specification<ShopEntity> specification);
    PaginateDTO<ShopDTO> getShops(Integer page, Integer parPage, GenericSpecification<ShopEntity> specification);

    String getCurrentDevice(Long shopId);
}
