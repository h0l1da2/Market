package com.wemake.market.exception;

public class CouponErrorException extends Exception {
    public CouponErrorException() {
    }

    public CouponErrorException(String message) {
        super(message);
    }

    public CouponErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public CouponErrorException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getMessage() {
        return "쿠폰을 다시 확인해주세요.";
    }
}
