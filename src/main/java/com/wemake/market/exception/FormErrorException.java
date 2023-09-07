package com.wemake.market.exception;

public class FormErrorException extends Exception {
    public FormErrorException() {
    }

    public FormErrorException(String message) {
        super(message);
    }

    public FormErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public FormErrorException(Throwable cause) {
        super(cause);
    }

    public String getErrorMessage() {
        return "양식을 다시 확인해주세요.";
    }
}
