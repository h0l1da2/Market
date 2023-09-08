package com.wemake.market.handler;

import com.wemake.market.exception.DuplicateItemException;
import com.wemake.market.exception.ItemNotFoundException;
import com.wemake.market.exception.UnavailableDateTimeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ItemExceptionController {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ItemNotFoundException.class)
    public ResponseEntity<String> itemNotFoundExceptionHandler(ItemNotFoundException e) {
        return ResponseEntity.badRequest()
                .body(e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DuplicateItemException.class)
    public ResponseEntity<String> duplicateItemExceptionHandler(DuplicateItemException e) {
        return ResponseEntity.badRequest()
                .body(e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UnavailableDateTimeException.class)
    public ResponseEntity<String> unavailableDateTimeExceptionHandler(UnavailableDateTimeException e) {
        return ResponseEntity.badRequest()
                .body(e.getMessage());
    }
    
}
