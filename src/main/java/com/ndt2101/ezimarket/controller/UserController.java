package com.ndt2101.ezimarket.controller;

import com.ndt2101.ezimarket.base.BaseController;
import com.ndt2101.ezimarket.dto.PasswordChangeDTO;
import com.ndt2101.ezimarket.exception.ApplicationException;
import com.ndt2101.ezimarket.service.UserService;
import com.ndt2101.ezimarket.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/user")
public class UserController extends BaseController<Object> {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtils jwtUtils;

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
}
