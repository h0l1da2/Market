package com.wemake.market.exception;

public class DuplicateCouponException extends Exception {
    public DuplicateCouponException() {
    }

    public DuplicateCouponException(String message) {
        super(message);
    }

    public DuplicateCouponException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateCouponException(Throwable cause) {
        super(cause);
    }

    public String getErrorMessage() {
        return "해당 아이템으로는 이미 쿠폰이 발행되어 있습니다.";
    }
}
