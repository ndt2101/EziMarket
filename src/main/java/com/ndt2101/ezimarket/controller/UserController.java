package com.ndt2101.ezimarket.controller;

import com.ndt2101.ezimarket.base.BaseController;
import com.ndt2101.ezimarket.dto.AddressDTO;
import com.ndt2101.ezimarket.dto.CurrentDeviceDTO;
import com.ndt2101.ezimarket.dto.PasswordChangeDTO;
import com.ndt2101.ezimarket.exception.ApplicationException;
import com.ndt2101.ezimarket.model.AddressEntity;
import com.ndt2101.ezimarket.model.ShopEntity;
import com.ndt2101.ezimarket.repository.ShopRepository;
import com.ndt2101.ezimarket.service.UserService;
import com.ndt2101.ezimarket.utils.JwtUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/user")
public class UserController extends BaseController<Object> {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private ShopRepository shopRepository;
    @Autowired
    private ModelMapper mapper;


    @PutMapping("/password/change")
    public ResponseEntity<?> changePassword(@RequestBody @Valid PasswordChangeDTO passwordChangeDTO, @RequestHeader(name = "Authorization") String token) throws Exception {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
            String loginName = jwtUtils.getLoginNameFromToken(token);
            return this.successfulResponse(userService.passwordChange(passwordChangeDTO, loginName));
        } else {
            throw new ApplicationException(HttpStatus.FORBIDDEN, "Response with unauthorized error");
        }
    }

    @PutMapping("/address")
    public ResponseEntity<?> setAddress(@RequestBody AddressDTO addressDTO, @RequestHeader(name = "Authorization") String token) throws Exception {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
            String loginName = jwtUtils.getLoginNameFromToken(token);
            return this.successfulResponse(userService.setLocation(addressDTO, loginName));
        } else {
            throw new ApplicationException(HttpStatus.FORBIDDEN, "Response with unauthorized error");
        }
    }

    @GetMapping("/address")
    public ResponseEntity<?> getAddress(
            @RequestHeader(name = "Authorization") String token,
            @RequestParam(name = "shopId", required = false) Long shopId) throws Exception {
        if (shopId != null) {
            return this.successfulResponse(mapper.map(shopRepository.findById(shopId).get().getUserLoginData().getAddress(), AddressDTO.class));
        } else if (token.startsWith("Bearer ")) {
            token = token.substring(7);
            String loginName = jwtUtils.getLoginNameFromToken(token);
            return this.successfulResponse(userService.getLocation(loginName));
        } else {
            throw new ApplicationException(HttpStatus.FORBIDDEN, "Response with unauthorized error");
        }
    }

    @PostMapping("/current_device")
    public ResponseEntity<?> setUserDevice(@RequestBody CurrentDeviceDTO currentDeviceDTO) {
        return successfulResponse(userService.setCurrentDevice(currentDeviceDTO));
    }

    @GetMapping("/current_device/")
    public ResponseEntity<?> getUserDevice(@RequestParam(name = "userId") Long userId) {
        return successfulResponse(userService.getCurrentDevice(userId));
    }


}
