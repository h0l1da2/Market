package com.wemake.market.handler;

import com.wemake.market.exception.NotAuthorityException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionController {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NotAuthorityException.class)
    public ResponseEntity<String> haveNotMarketAuthExceptionHandler(NotAuthorityException e) {
        return ResponseEntity.badRequest()
                .body(e.getErrorMessage());
    }
}
