package com.ndt2101.ezimarket.base;

import com.ndt2101.ezimarket.constant.Common;
import com.ndt2101.ezimarket.dto.ResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseController<T> {

    public ResponseEntity<?> successfulResponse(T data) {
//        Map<String, T> result = new HashMap<>();
//        result.put("data", data);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDTO<>(HttpStatus.OK.value(), Common.SUCCESSFUL_RESPONSE, data));
    }

    public ResponseEntity<?> successfulListResponse(List<T> metaData) {
//        Map<String, List<T>> result = new HashMap<>();
//        result.put("data", metaData);
        return ResponseEntity.ok(new ResponseDTO<>(HttpStatus.OK.value(), Common.SUCCESSFUL_RESPONSE, metaData));
    }

    public ResponseEntity<?> unsuccessfulResponse(T error, HttpStatus httpStatus) {
        Map<String, T> result = new HashMap<>();
//        result.put("error", error);
        return ResponseEntity.status(httpStatus).body(new ResponseDTO<>(httpStatus.value(), Common.UNSUCCESSFUL_RESPONSE, error));
    }

    public ResponseEntity<?> unsuccessfulListResponse(List<T> metaError, HttpStatus httpStatus) {
        Map<String, List<T>> result = new HashMap<>();
//        result.put("error", metaError);
        return ResponseEntity.status(httpStatus).body(new ResponseDTO<>(httpStatus.value(), Common.UNSUCCESSFUL_RESPONSE, metaError));
    }
}
