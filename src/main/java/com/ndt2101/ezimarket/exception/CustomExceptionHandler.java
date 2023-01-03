package com.ndt2101.ezimarket.exception;

import com.ndt2101.ezimarket.base.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class CustomExceptionHandler extends BaseController<Object> {

    //    MessagingException

    @ExceptionHandler(BindException.class)
    public ResponseEntity<?> handleBindException(BindException exception) {
        StringBuffer message = new StringBuffer("");
        List<Object> errors = exception.getBindingResult().getFieldErrors()
                .stream()
                .map((objectError -> {
                    message.append("In ").append(objectError.getField()).append(": ").append(objectError.getDefaultMessage()).append("\n");
                    return new InvalidResponse(objectError.getField(), objectError.getDefaultMessage());
                }))
                .collect(Collectors.toList());
        log.error("Valid exception with message: \n{}", message);
        return this.unsuccessfulListResponse(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handleUsernameNotFoundException(NotFoundException exception) {
        log.error("Not found exception with message: \n{}", exception.getMessage());
        return this.unsuccessfulResponse(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<?> handleNotFoundException(ApplicationException exception) {
        log.error("Application exception with message: \n{}", exception.getMessage());
        return this.unsuccessfulResponse(exception.getMessage(), exception.getStatus());
    }

//    @ExceptionHandler(InternalAuthenticationServiceException.class)
//    public ResponseEntity<?> handleAuthenticationException(InternalAuthenticationServiceException exception) {
//        log.error("Authentication exception with message: \n{}", exception.getMessage());
//        return this.unsuccessfulResponse(exception.getMessage(), HttpStatus.UNAUTHORIZED);
//    }

//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<?> handleServerException(Exception exception) {
//        log.error("Internal server exception:\n{}", exception.getMessage());
//        exception.printStackTrace();
//        return this.unsuccessfulResponse("Server internal exception", HttpStatus.INTERNAL_SERVER_ERROR);
//    }
}
