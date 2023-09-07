package com.wemake.market.handler;

import com.wemake.market.exception.DuplicateCouponException;
import com.wemake.market.exception.ItemNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CouponExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DuplicateCouponException.class)
    public ResponseEntity<String> duplicateCouponExceptionHandler(DuplicateCouponException e) {
        return ResponseEntity.badRequest()
                .body(e.getErrorMessage());
    }

}
