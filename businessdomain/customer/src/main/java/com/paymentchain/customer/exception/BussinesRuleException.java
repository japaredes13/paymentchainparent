package com.paymentchain.customer.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

@EqualsAndHashCode(callSuper = true)
@Data
public class BussinesRuleException extends Exception {
    private Long id;
    private String code;
    private HttpStatus httpStatus;


    public BussinesRuleException(String message, Long id, String code, HttpStatus httpStatus) {
        super(message);
        this.id = id;
        this.code = code;
        this.httpStatus = httpStatus;
    }

    public BussinesRuleException(String message, String code, HttpStatus httpStatus) {
        super(message);
        this.code = code;
        this.httpStatus = httpStatus;
    }

    public BussinesRuleException(String message, Throwable cause, Long id, String code, HttpStatus httpStatus) {
        super(message, cause);
    }
}
