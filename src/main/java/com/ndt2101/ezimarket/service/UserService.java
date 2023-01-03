package com.ndt2101.ezimarket.service;

import com.ndt2101.ezimarket.dto.PasswordChangeDTO;
import com.ndt2101.ezimarket.exception.ApplicationException;

public interface UserService {
    String passwordChange(PasswordChangeDTO passwordChangeDTO, String loginName) throws Exception;
}
