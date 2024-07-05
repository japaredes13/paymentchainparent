package com.paymentchain.customer.exception;

import com.paymentchain.customer.common.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<?> handleUnknowHostException(Exception ex) {
        ExceptionResponse exception = new ExceptionResponse("TECNICO", "Input Ouput error", "1024", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception);
    }

    @ExceptionHandler(value = BussinesRuleException.class)
    public ResponseEntity<?> handleBussinesRuleException(BussinesRuleException ex) {
        ExceptionResponse exception = new ExceptionResponse("BUSSINES", "Error de Validacion", ex.getCode(), ex.getMessage());
        return ResponseEntity.status(ex.getHttpStatus()).body(exception);
    }
}
