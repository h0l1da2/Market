package com.wemake.market.exception;

public class NotAuthorityException extends Exception {

    public NotAuthorityException() {
    }

    public NotAuthorityException(String message) {
        super(message);
    }

    public NotAuthorityException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotAuthorityException(Throwable cause) {
        super(cause);
    }

    public String getErrorMessage() {
        return "해당 권한이 없음";
    }
}
