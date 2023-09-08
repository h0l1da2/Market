package com.wemake.market.exception;

public class DuplicateItemException extends Exception {

    public DuplicateItemException() {
    }

    public DuplicateItemException(String message) {
        super(message);
    }

    public DuplicateItemException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateItemException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getMessage() {
        return "상품명이 중복입니다.";
    }
}
