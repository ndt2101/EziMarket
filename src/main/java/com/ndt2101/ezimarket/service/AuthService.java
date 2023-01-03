package com.ndt2101.ezimarket.service;

import com.ndt2101.ezimarket.dto.*;
import com.ndt2101.ezimarket.exception.ApplicationException;

public interface AuthService {
    String register(UserDTO userDTO) throws Exception;

    AuthenticationResponse login(AuthenticationRequest request) throws Exception;

    AuthenticationResponse loginWithGoogle(String token) throws Exception;
    String resetPassword(ResetUserPasswordDTO resetUserPasswordDTO);

    String verify(VerifiedRequestDTO verifiedRequestDTO) throws ApplicationException;

    String resendVerifiedToken(ResendVerifiedTokenDTO email);
}
