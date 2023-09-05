package com.wemake.market.exception;

public class UnavailableDateTimeException extends Exception {
    public UnavailableDateTimeException() {
    }

    public UnavailableDateTimeException(String message) {
        super(message);
    }

    public UnavailableDateTimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnavailableDateTimeException(Throwable cause) {
        super(cause);
    }

    public String getErrorMessage() {
        return "존재하지 않아 볼 수 없는 시간입니다. 올바른 시간을 확인하세요.";
    }
}
