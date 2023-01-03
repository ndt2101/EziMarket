package com.ndt2101.ezimarket.controller;

import com.ndt2101.ezimarket.base.BaseController;
import com.ndt2101.ezimarket.dto.*;
import com.ndt2101.ezimarket.exception.ApplicationException;
import com.ndt2101.ezimarket.service.AuthService;
import com.ndt2101.ezimarket.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController extends BaseController<Object> {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid AuthenticationRequest request) throws Exception {
        return this.successfulResponse(authService.login(request));
    }

    @PostMapping("/login_with_google")
    public ResponseEntity<?> loginWithGoogle(@RequestBody ExternalAuthenticationRequest request) throws Exception {
        return this.successfulResponse(authService.loginWithGoogle(request.getToken()));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid UserDTO userDTO) throws Exception {
        return this.successfulResponse(authService.register(userDTO));
    }

    @PutMapping("/password/forget")
    public ResponseEntity<?> forgotPassword(@RequestBody @Valid ResetUserPasswordDTO resetUserPasswordDTO) {
        return this.successfulResponse(authService.resetPassword(resetUserPasswordDTO));
    }

    @PutMapping("/verify")
    public ResponseEntity<?> verify(@RequestBody @Valid VerifiedRequestDTO verifiedRequestDTO) throws ApplicationException {
        return this.successfulResponse(authService.verify(verifiedRequestDTO));
    }

    @PutMapping("/verify/retry")
    public ResponseEntity<?> resendVerifiedToken(@RequestBody @Valid ResendVerifiedTokenDTO resendVerifiedTokenDTO) throws ApplicationException {
        return this.successfulResponse(authService.resendVerifiedToken(resendVerifiedTokenDTO));
    }
}
