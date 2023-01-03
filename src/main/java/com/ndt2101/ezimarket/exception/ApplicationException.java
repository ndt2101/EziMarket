package com.ndt2101.ezimarket.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ApplicationException extends Exception{
    private HttpStatus status;
    private String message;
}
