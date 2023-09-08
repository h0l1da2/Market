package com.wemake.market.exception;

public class CouponNotFoundException extends Exception {
    public CouponNotFoundException() {
    }

    public CouponNotFoundException(String message) {
        super(message);
    }

    public CouponNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public CouponNotFoundException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getMessage() {
        return "해당 쿠폰을 찾을 수 없습니다.";
    }
}
