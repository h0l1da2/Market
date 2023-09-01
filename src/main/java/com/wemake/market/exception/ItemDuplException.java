package com.wemake.market.exception;

public class ItemDuplException extends Exception {

    public ItemDuplException() {
    }

    public ItemDuplException(String message) {
        super(message);
    }

    public ItemDuplException(String message, Throwable cause) {
        super(message, cause);
    }

    public ItemDuplException(Throwable cause) {
        super(cause);
    }
}
