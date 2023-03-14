package com.ndt2101.ezimarket.service;

import com.ndt2101.ezimarket.dto.AddressDTO;
import com.ndt2101.ezimarket.dto.CurrentDeviceDTO;
import com.ndt2101.ezimarket.dto.PasswordChangeDTO;
import com.ndt2101.ezimarket.exception.ApplicationException;

import java.util.List;

public interface UserService {
    String passwordChange(PasswordChangeDTO passwordChangeDTO, String loginName) throws Exception;
    String setLocation(AddressDTO addressDTO, String loginName);
    String setCurrentDevice(CurrentDeviceDTO currentDeviceDTO);
    AddressDTO getLocation(String loginName);

    String getCurrentDevice(Long userId);
}
