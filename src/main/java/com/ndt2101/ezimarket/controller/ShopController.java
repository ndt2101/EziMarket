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
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.JoinType;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/shop")
public class ShopController extends BaseController<Object> {
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private ShopService shopService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody ShopDTO shopDTO, @RequestHeader(name = "Authorization") String token) throws ApplicationException, ExecutionException, InterruptedException {
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

    @GetMapping("/")
    public ResponseEntity<?> getShops(
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "perPage", required = false) Integer perPage,
            HttpServletRequest request
    ) {
        GenericSpecification<ShopEntity> specification = new GenericSpecification<ShopEntity>();
        specification = specification.getBasicQuery(request);
        return resPagination(shopService.getShops(page, perPage, specification));
    }
}
