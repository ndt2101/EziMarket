package com.ndt2101.ezimarket.service.impl;

import com.ndt2101.ezimarket.base.BasePagination;
import com.ndt2101.ezimarket.constant.Common;
import com.ndt2101.ezimarket.dto.GHN.GHNResponse;
import com.ndt2101.ezimarket.dto.GHN.Store;
import com.ndt2101.ezimarket.dto.ShopDTO;
import com.ndt2101.ezimarket.dto.pagination.PaginateDTO;
import com.ndt2101.ezimarket.dto.product.ProductResponseDTO;
import com.ndt2101.ezimarket.exception.ApplicationException;
import com.ndt2101.ezimarket.exception.NotFoundException;
import com.ndt2101.ezimarket.model.ShopEntity;
import com.ndt2101.ezimarket.model.UserLoginDataEntity;
import com.ndt2101.ezimarket.repository.RoleRepository;
import com.ndt2101.ezimarket.repository.ShopRepository;
import com.ndt2101.ezimarket.repository.UserRepository;
import com.ndt2101.ezimarket.service.ShopService;
import com.ndt2101.ezimarket.specification.GenericSpecification;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.mail.MessagingException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
public class ShopServiceImpl extends BasePagination<ShopEntity, ShopRepository> implements ShopService {

    @Autowired
    private ShopRepository shopRepository;
    @Autowired
    private ModelMapper mapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    public ShopServiceImpl(ShopRepository repository) {
        super(repository);
    }
    @Override
    public String register(ShopDTO shopDTO, String loginName) throws ExecutionException, InterruptedException {
        UserLoginDataEntity userLoginData = userRepository.findByLoginName(loginName).orElseThrow(() -> new NotFoundException("User " + loginName + " not found"));
        userLoginData.setRole(roleRepository.getByDescription("ROLE_SHOP")
                .orElseThrow(() -> new NotFoundException("Role shop not found")));
        ShopEntity shopEntity = mapper.map(shopDTO, ShopEntity.class);
        shopEntity.setUserLoginData(userLoginData);
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        Callable<Integer> createStore = new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                RestTemplate restTemplate = new RestTemplate();
                Store store = new Store();
                store.setName(shopDTO.getName());
                store.setAddress(userLoginData.getAddress().getDetailAddress());
                store.setDistrict_id(userLoginData.getAddress().getDistrict().getDistrictID());
                store.setPhone(userLoginData.getPhone());
                store.setWard_code(userLoginData.getAddress().getWard().getWardCode());

                // Tạo header
                HttpHeaders headers = new HttpHeaders();
                headers.set("Token", Common.GHN_TOKEN);
                headers.set("Content-Type", Common.GHN_CONTENT_TYPE);

                // Tạo entity từ đối tượng request và header
                HttpEntity<Store> entity = new HttpEntity<>(store, headers);

                ResponseEntity<GHNResponse> savedStore = restTemplate.exchange(Common.CREATE_STORE_IN_GHN_API,  HttpMethod.POST, entity, GHNResponse.class);
                return Objects.requireNonNull((LinkedHashMap<String, Integer>)savedStore.getBody().getData()).get("shop_id");
            }
        };
        Future<Integer> future = executorService.submit(createStore);
        Integer storeId;
        try {
            storeId = future.get();
            shopEntity.setGHNStoreId(storeId);
            shopRepository.save(shopEntity);
            return "Register shop successfully";
        } catch (InterruptedException | ExecutionException e) {
            throw e;
        }
    }

    @Override
    public ShopDTO getShop(Specification<ShopEntity> specification) {
        ShopEntity shopEntity = shopRepository.findOne(specification).orElseThrow(() -> new NotFoundException("Shop not found"));
        ShopDTO shopDTO = mapper.map(shopEntity, ShopDTO.class);
        shopDTO.setAvatar(shopEntity.getUserLoginData().getAvatarUrl());
        return shopDTO;
    }

    @Override
    public PaginateDTO<ShopDTO> getShops(Integer page, Integer perPage, GenericSpecification<ShopEntity> specification) {
        PaginateDTO<ShopEntity> shopEntityPaginateDTO = this.paginate(page, perPage, specification);
        List<ShopDTO> shopDTOs = shopEntityPaginateDTO.getPageData().stream().map(shopEntity -> {
            ShopDTO shopDTO = mapper.map(shopEntity, ShopDTO.class);
            shopDTO.setAvatar(shopEntity.getUserLoginData().getAvatarUrl());
            shopDTO.setId(shopEntity.getUserLoginData().getId()); // lay id cua user
            return shopDTO;
        }).toList();
        Page<ShopDTO> pageData = new PageImpl<>(shopDTOs, PageRequest.of(page, perPage), perPage);
        return new PaginateDTO<>(pageData, shopEntityPaginateDTO.getPagination());
    }

    @Override
    public String getCurrentDevice(Long shopId) {
        return shopRepository.findById(shopId).orElseThrow(Common.shopNotFound).getUserLoginData().getCurrentDevice();
    }
}
