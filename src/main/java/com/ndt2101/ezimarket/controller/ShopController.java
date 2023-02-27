package com.ndt2101.ezimarket.controller;

import com.ndt2101.ezimarket.base.BaseController;
import com.ndt2101.ezimarket.dto.ShopDTO;
import com.ndt2101.ezimarket.exception.ApplicationException;
import com.ndt2101.ezimarket.model.ShopEntity;
import com.ndt2101.ezimarket.service.ShopService;
import com.ndt2101.ezimarket.specification.GenericSpecification;
import com.ndt2101.ezimarket.specification.JoinCriteria;
import com.ndt2101.ezimarket.specification.SearchCriteria;
import com.ndt2101.ezimarket.specification.SearchOperation;
import com.ndt2101.ezimarket.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.JoinType;

@RestController
@RequestMapping("/api/shop")
public class ShopController extends BaseController<Object> {
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private ShopService shopService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody ShopDTO shopDTO, @RequestHeader(name = "Authorization") String token) throws ApplicationException {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
            String loginName = jwtUtils.getLoginNameFromToken(token);
            return this.successfulResponse(shopService.register(shopDTO, loginName));
        } else {
            throw new ApplicationException(HttpStatus.FORBIDDEN, "Response with unauthorized error");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getShop(@PathVariable(name = "id") Long id, @RequestParam(name = "type") String idType) {
        GenericSpecification<ShopEntity> specification = new GenericSpecification<>();
        if (idType.equals("userId")) {
            specification.buildJoin(new JoinCriteria(SearchOperation.EQUAL, "userLoginData", "id", id, JoinType.LEFT));
        } else if (idType.equals("shopId")) {
            specification.add(new SearchCriteria("id", id, SearchOperation.EQUAL));
        }
        return successfulResponse(shopService.getShop(specification));
    }
}
